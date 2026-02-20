import { Component, inject, output } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationCardComponent } from '../notification-card/notification-card.component';

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [
    NotificationCardComponent,
  ],
  template: `
    <div class="fixed inset-0 z-50 flex flex-col bg-white sm:static sm:w-[380px] sm:max-h-[500px] sm:rounded-xl sm:shadow-xl overflow-y-auto" (click)="$event.stopPropagation()">
      <!-- Header -->
      <div class="flex items-center justify-between px-5 py-4">
        <span class="text-base font-semibold text-surface-800">Notifications</span>
        <div class="flex items-center gap-2">
          @if (notificationService.unreadCount() > 0) {
            <button
              class="w-8 h-8 rounded-full bg-[#2e7d32] hover:bg-[#388e3c] text-white flex items-center justify-center cursor-pointer border-none transition-colors"
              title="Mark all as read"
              (click)="notificationService.markAllAsRead()"
            >
              <i class="pi pi-list-check text-sm"></i>
            </button>
          }
          <button
            class="sm:hidden w-8 h-8 rounded-full bg-surface-100 hover:bg-surface-200 text-surface-600 flex items-center justify-center cursor-pointer border-none transition-colors"
            (click)="close.emit()"
          >
            <i class="pi pi-times text-sm"></i>
          </button>
        </div>
      </div>

      <div class="border-t border-surface-100"></div>

      @if (notificationService.notifications().length === 0) {
        <div class="py-10 text-center text-surface-400 text-sm">No notifications</div>
      } @else {
        <div class="flex flex-col py-1">
          @for (notification of notificationService.notifications(); track notification.id) {
            <app-notification-card
              [notification]="notification"
              (markRead)="notificationService.markAsRead($event)"
              (remove)="notificationService.deleteNotification($event)"
            />
          }
        </div>
      }
    </div>
  `,
})
export class NotificationPanelComponent {
  notificationService = inject(NotificationService);
  close = output();
}
