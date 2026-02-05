import { Component, computed, input, output, signal } from '@angular/core';
import { Task } from '../../models/task.model';
import { LiveCountdownComponent } from '../../../../shared/components/live-countdown/live-countdown.component';
import { RelativeTimeComponent } from '../../../../shared/components/relative-time/relative-time.component';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [LiveCountdownComponent, RelativeTimeComponent],
  template: `
    <div class="bg-white rounded-xl p-5 shadow-sm border border-surface-100 flex flex-col gap-3">
      <!-- Header: name + actions -->
      <div class="flex items-start justify-between gap-2">
        <h3 class="text-lg font-semibold text-surface-800 m-0">{{ task().name }}</h3>
        <div class="flex items-center gap-2">
          <button
            class="text-surface-400 hover:text-surface-600 cursor-pointer bg-transparent border-none p-0 text-lg leading-none"
            (click)="edit.emit(task().id)"
          >
            <i class="pi pi-pencil"></i>
          </button>
          <button
            class="text-surface-400 hover:text-surface-600 cursor-pointer bg-transparent border-none p-0 text-lg leading-none"
            (click)="delete.emit(task().id)"
          >
            <i class="pi pi-times"></i>
          </button>
        </div>
      </div>

      <!-- Description -->
      @if (task().description) {
        <p class="text-sm text-surface-500 m-0">{{ task().description }}</p>
      }

      <!-- Footer: status + deadline -->
      <div class="flex items-center justify-between mt-auto pt-1">
        <!-- Status badge (clickable to toggle) -->
        @if (task().completed) {
          <button
            class="text-xs font-bold uppercase px-2.5 py-1 rounded bg-[#e8f5e9] text-[#2e7d32] border-none cursor-pointer hover:bg-[#c8e6c9] transition-colors"
            title="Click to reopen"
            (click)="toggle.emit(task().id)"
          >
            Completed
          </button>
        } @else if (isOverdue()) {
          <button
            class="text-xs font-bold uppercase px-2.5 py-1 rounded bg-red-100 text-red-600 border-none cursor-pointer hover:bg-red-200 transition-colors"
            title="Click to mark as completed"
            (click)="toggle.emit(task().id)"
          >
            Overdue
          </button>
        } @else {
          <button
            class="text-xs font-bold uppercase px-2.5 py-1 rounded bg-amber-100 text-amber-700 border-none cursor-pointer hover:bg-amber-200 transition-colors"
            title="Click to mark as completed"
            (click)="toggle.emit(task().id)"
          >
            In Progress
          </button>
        }

        <!-- Time info -->
        @if (task().endsIn) {
          @if (task().completed) {
            <span class="text-xs text-surface-500">
              <app-relative-time [targetDate]="task().endsIn!" />
            </span>
          } @else {
            <span class="text-xs font-medium">
              <app-live-countdown
                [targetDate]="task().endsIn!"
                (overdueChange)="onOverdueChange($event)"
              />
            </span>
          }
        } @else {
          <span class="text-xs text-surface-400 italic">
            No deadline
          </span>
        }
      </div>
    </div>
  `,
})
export class TaskCardComponent {
  task = input.required<Task>();
  edit = output<string>();
  toggle = output<string>();
  delete = output<string>();

  // Signal for real-time overdue status updates
  private overdueStatus = signal<boolean | null>(null);

  // Use signal value if available, otherwise compute from task data
  isOverdue = computed(() => {
    const overrideValue = this.overdueStatus();
    if (overrideValue !== null) {
      return overrideValue;
    }
    const t = this.task();
    if (t.completed || !t.endsIn) return false;
    return new Date(t.endsIn) < new Date();
  });

  onOverdueChange(isOverdue: boolean): void {
    this.overdueStatus.set(isOverdue);
  }
}
