import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Notification } from '../../../core/models/notification.model';

@Component({
  selector: 'app-notification-card',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div
      class="flex items-start gap-3 mx-2 my-1 px-4 py-3 rounded-lg transition-colors"
      [class]="notification().read ? 'bg-surface-50' : 'bg-amber-50'"
    >
      <div class="flex-1 min-w-0">
        <div class="font-semibold text-sm text-surface-800">{{ notification().title }}</div>
        <div class="text-xs text-surface-500 mt-1 leading-relaxed">{{ notification().message }}</div>
        <span class="inline-block mt-2 text-xs font-medium text-surface-500">
          {{ notification().createdAt | date:'MMM d, h:mm a' }}
        </span>
      </div>

      <div class="flex flex-col items-center gap-1 shrink-0">
        @if (!notification().read) {
          <button
            class="text-surface-400 hover:text-[#2e7d32] bg-transparent border-none cursor-pointer p-1 rounded-full hover:bg-surface-100 transition-colors"
            title="Mark as read"
            (click)="markRead.emit(notification().id)"
          >
            <i class="pi pi-check-circle text-sm"></i>
          </button>
        }
      </div>
    </div>
  `,
})
export class NotificationCardComponent {
  notification = input.required<Notification>();
  markRead = output<string>();
  remove = output<string>();
}
