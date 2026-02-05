import { Component, inject } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationCardComponent } from '../notification-card/notification-card.component';

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [
    NotificationCardComponent,
  ],
  template: `
    <div class="w-[380px] max-h-[500px] overflow-y-auto bg-white" (click)="$event.stopPropagation()">
      <!-- Header -->
      <div class="flex items-center justify-between px-5 py-4">
        <span class="text-base font-semibold text-surface-800">Notifications</span>
        @if (notificationService.unreadCount() > 0) {
          <button
            class="w-8 h-8 rounded-full bg-[#2e7d32] hover:bg-[#388e3c] text-white flex items-center justify-center cursor-pointer border-none transition-colors"
            title="Mark all as read"
            (click)="notificationService.markAllAsRead()"
          >
            <i class="pi pi-list-check text-sm"></i>
          </button>
        }
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
}
