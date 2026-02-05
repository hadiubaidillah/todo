import { inject, Injectable, NgZone, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { Notification } from '../models/notification.model';
import { environment } from '../../../environments/environment';
import { catchError, forkJoin, of, retry, Subject } from 'rxjs';

// Notification types that indicate task status changes
const TASK_STATUS_CHANGE_TYPES = ['OVERDUE', 'COMPLETED', 'EXTENDED'];

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly baseUrl = `${environment.apiGatewayUrl}/notifications`;

  notifications = signal<Notification[]>([]);
  unreadCount = signal<number>(0);
  isInitialized = signal<boolean>(false);

  // Observable for task status change notifications
  private taskStatusChange$ = new Subject<Notification>();
  onTaskStatusChange = this.taskStatusChange$.asObservable();

  private abortController: AbortController | null = null;
  private reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
  private reconnectDelay = 1000;
  private readonly maxReconnectDelay = 30000;
  private isDisconnected = false;
  private isFirstConnection = true;

  private http = inject(HttpClient);
  private keycloak = inject(KeycloakService);
  private zone = inject(NgZone);

  /**
   * Initialize notifications: load initial data first, then connect SSE.
   * This prevents race condition where SSE data arrives before initial load completes.
   */
  initialize(): void {
    console.log('[Notification] initialize called, isInitialized:', this.isInitialized(), 'isDisconnected:', this.isDisconnected);

    // If already initialized, just reconnect SSE if it was disconnected
    if (this.isInitialized()) {
      if (this.isDisconnected) {
        console.log('[Notification] Reconnecting SSE after navigation...');
        this.isDisconnected = false;
        this.isFirstConnection = false; // This is a reconnect, so reload data
        this.connectSSE();
      }
      return;
    }

    console.log('[Notification] Loading initial data...');
    forkJoin({
      notifications: this.http.get<Notification[]>(this.baseUrl).pipe(
        retry({ count: 2, delay: 1000 }),
        catchError((err) => {
          console.error('Failed to load notifications', err);
          return of([] as Notification[]);
        })
      ),
      unreadCount: this.http.get<{ count: number }>(`${this.baseUrl}/unread/count`).pipe(
        retry({ count: 2, delay: 1000 }),
        catchError((err) => {
          console.error('Failed to load unread count', err);
          return of({ count: 0 });
        })
      ),
    }).subscribe({
      next: ({ notifications, unreadCount }) => {
        console.log('[Notification] Initial data loaded:', notifications.length, 'notifications, unread:', unreadCount.count);
        this.notifications.set(notifications);
        this.unreadCount.set(unreadCount.count);
        this.isInitialized.set(true);
        // Only connect SSE after initial data is loaded
        console.log('[Notification] Starting SSE connection...');
        this.connectSSE();
      },
    });
  }

  loadNotifications(): void {
    this.http.get<Notification[]>(this.baseUrl).pipe(
      retry({ count: 2, delay: 1000 }),
      catchError((err) => {
        console.error('Failed to load notifications', err);
        return of([] as Notification[]);
      })
    ).subscribe({
      next: (data) => this.notifications.set(data),
    });
  }

  loadUnreadCount(): void {
    this.http.get<{ count: number }>(`${this.baseUrl}/unread/count`).pipe(
      retry({ count: 2, delay: 1000 }),
      catchError((err) => {
        console.error('Failed to load unread count', err);
        return of({ count: 0 });
      })
    ).subscribe({
      next: (data) => this.unreadCount.set(data.count),
    });
  }

  /**
   * Reload notifications and unread count.
   * Called after SSE reconnect to catch any missed notifications.
   * Uses merge strategy to avoid losing notifications received via SSE during reload.
   */
  private reloadData(): void {
    forkJoin({
      notifications: this.http.get<Notification[]>(this.baseUrl).pipe(
        retry({ count: 2, delay: 1000 }),
        catchError((err) => {
          console.error('Failed to reload notifications', err);
          return of(null);
        })
      ),
      unreadCount: this.http.get<{ count: number }>(`${this.baseUrl}/unread/count`).pipe(
        retry({ count: 2, delay: 1000 }),
        catchError((err) => {
          console.error('Failed to reload unread count', err);
          return of(null);
        })
      ),
    }).subscribe({
      next: ({ notifications, unreadCount }) => {
        if (notifications !== null) {
          // Merge: keep any SSE notifications not in server response, then add server data
          const currentNotifications = this.notifications();
          const serverIds = new Set(notifications.map(n => n.id));
          const sseOnlyNotifications = currentNotifications.filter(n => !serverIds.has(n.id));
          // Combine: SSE-only notifications first (newest), then server notifications
          this.notifications.set([...sseOnlyNotifications, ...notifications]);
        }
        if (unreadCount !== null) {
          // Recalculate unread count from merged list to be accurate
          const actualUnread = this.notifications().filter(n => !n.read).length;
          this.unreadCount.set(actualUnread);
        }
      },
    });
  }

  markAsRead(id: string): void {
    this.http.put<Notification>(`${this.baseUrl}/${id}/read`, {}).subscribe({
      next: () => {
        this.notifications.update((list) =>
          list.map((n) => (n.id === id ? { ...n, read: true } : n))
        );
        this.unreadCount.update((c) => Math.max(0, c - 1));
      },
    });
  }

  markAllAsRead(): void {
    this.http.put<void>(`${this.baseUrl}/read-all`, {}).subscribe({
      next: () => {
        this.notifications.update((list) =>
          list.map((n) => ({ ...n, read: true }))
        );
        this.unreadCount.set(0);
      },
    });
  }

  deleteNotification(id: string): void {
    this.http.delete(`${this.baseUrl}/${id}`).subscribe({
      next: () => {
        const removed = this.notifications().find((n) => n.id === id);
        this.notifications.update((list) => list.filter((n) => n.id !== id));
        if (removed && !removed.read) {
          this.unreadCount.update((c) => Math.max(0, c - 1));
        }
      },
    });
  }

  async connectSSE(): Promise<void> {
    // Don't connect if explicitly disconnected or not initialized
    if (this.isDisconnected || !this.isInitialized()) return;

    // Clean up existing connection
    if (this.abortController) {
      this.abortController.abort();
      this.abortController = null;
    }

    try {
      const token = await this.keycloak.getToken();
      if (!token) {
        throw new Error('No token available');
      }

      this.abortController = new AbortController();

      const response = await fetch(`${this.baseUrl}/stream`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache',
        },
        signal: this.abortController.signal,
      });

      if (!response.ok || !response.body) {
        throw new Error(`SSE connection failed: ${response.status}`);
      }

      // Reset reconnect delay on successful connection
      this.reconnectDelay = 1000;
      console.log('[SSE] Connected successfully, isFirstConnection:', this.isFirstConnection);

      // On reconnect (not first connection), reload data to catch missed notifications
      if (!this.isFirstConnection) {
        console.log('[SSE] Reconnect detected, reloading data...');
        this.reloadData();
      }
      this.isFirstConnection = false;

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';
      let eventName = '';
      let dataLines: string[] = [];

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });
        console.log('[SSE] Raw chunk received:', JSON.stringify(chunk));
        buffer += chunk;
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';

        for (const line of lines) {
          if (line.startsWith('event:')) {
            eventName = line.slice(6).trim();
            console.log('[SSE] Event name:', eventName);
          } else if (line.startsWith('data:')) {
            dataLines.push(line.slice(5));
            console.log('[SSE] Data line added, eventName:', eventName);
          } else if (line === '') {
            console.log('[SSE] Empty line, processing event:', eventName, 'dataLines:', dataLines.length);
            if (eventName === 'notification' && dataLines.length > 0) {
              this.handleSseEvent(dataLines.join('\n'));
            }
            eventName = '';
            dataLines = [];
          }
        }
      }
    } catch (err: any) {
      if (err?.name === 'AbortError') {
        console.log('[SSE] Connection aborted (intentional disconnect)');
        return;
      }
      console.error('[SSE] Connection error, will reconnect...', err);
    }

    console.log('[SSE] Connection ended, isDisconnected:', this.isDisconnected);
    // Only reconnect if not explicitly disconnected
    if (!this.isDisconnected) {
      this.scheduleReconnect();
    }
  }

  disconnectSSE(): void {
    this.isDisconnected = true;
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }
    if (this.abortController) {
      this.abortController.abort();
      this.abortController = null;
    }
  }

  private handleSseEvent(raw: string): void {
    try {
      const notification: Notification = JSON.parse(raw);
      console.log('[SSE] Received notification:', notification.id, notification.title, notification.type);
      this.zone.run(() => {
        // Prevent duplicate: only add if not already in the list
        const exists = this.notifications().some((n) => n.id === notification.id);
        if (!exists) {
          this.notifications.update((list) => [notification, ...list]);
          if (!notification.read) {
            this.unreadCount.update((c) => c + 1);
          }
          console.log('[SSE] Added notification, new count:', this.unreadCount());

          // Emit task status change event for relevant notification types
          if (TASK_STATUS_CHANGE_TYPES.includes(notification.type)) {
            console.log('[SSE] Emitting task status change:', notification.type);
            this.taskStatusChange$.next(notification);
          }
        } else {
          console.log('[SSE] Duplicate notification ignored:', notification.id);
        }
      });
    } catch (e) {
      console.error('Failed to parse SSE notification', e);
    }
  }

  private scheduleReconnect(): void {
    if (this.isDisconnected || this.reconnectTimeout) return;

    // Add jitter (0-500ms) to prevent thundering herd
    const jitter = Math.random() * 500;
    const delay = this.reconnectDelay + jitter;

    this.reconnectTimeout = setTimeout(() => {
      this.reconnectTimeout = null;
      this.connectSSE();
    }, delay);

    // Exponential backoff
    this.reconnectDelay = Math.min(this.reconnectDelay * 2, this.maxReconnectDelay);
  }
}
