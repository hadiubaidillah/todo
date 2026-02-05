import { Component, computed, input, model, output } from '@angular/core';
import { Dialog } from 'primeng/dialog';
import { Task } from '../../models/task.model';

type TaskStatus = 'completed' | 'overdue' | 'in-progress';

@Component({
  selector: 'app-toggle-status-dialog',
  standalone: true,
  imports: [Dialog],
  template: `
    <p-dialog
      header="Change Task Status"
      [(visible)]="visible"
      [modal]="true"
      [style]="{ width: '400px' }"
      styleClass="!rounded-xl"
    >
      @if (task()) {
        <div class="text-sm text-surface-600">
          <p class="m-0 mb-4">
            Are you sure you want to change the status of
            <strong>"{{ task()!.name }}"</strong>?
          </p>

          <div class="flex items-center justify-center gap-3">
            <!-- Current status badge -->
            <span [class]="currentStatusClass()">
              {{ currentStatusLabel() }}
            </span>

            <!-- Arrow -->
            <i class="pi pi-arrow-right text-surface-400"></i>

            <!-- Target status badge -->
            <span [class]="targetStatusClass()">
              {{ targetStatusLabel() }}
            </span>
          </div>
        </div>
      }

      <ng-template #footer>
        <div class="flex justify-end gap-3">
          <button
            class="px-5 py-2.5 rounded-lg bg-surface-300 hover:bg-surface-400 text-surface-700 text-sm font-medium cursor-pointer border-none transition-colors"
            (click)="visible.set(false)"
          >
            Cancel
          </button>
          <button
            class="px-5 py-2.5 rounded-lg bg-[#2e7d32] hover:bg-[#388e3c] text-white text-sm font-medium cursor-pointer border-none transition-colors"
            (click)="onConfirm()"
          >
            Confirm
          </button>
        </div>
      </ng-template>
    </p-dialog>
  `,
})
export class ToggleStatusDialogComponent {
  visible = model(false);
  task = input<Task | null>(null);
  confirmed = output<void>();

  currentStatus = computed<TaskStatus>(() => {
    const t = this.task();
    if (!t) return 'in-progress';
    if (t.completed) return 'completed';
    if (t.endsIn && new Date(t.endsIn) < new Date()) return 'overdue';
    return 'in-progress';
  });

  targetStatus = computed<TaskStatus>(() => {
    const current = this.currentStatus();
    if (current === 'completed') {
      // When toggling from completed, check if it would be overdue
      const t = this.task();
      if (t?.endsIn && new Date(t.endsIn) < new Date()) return 'overdue';
      return 'in-progress';
    }
    return 'completed';
  });

  currentStatusLabel = computed(() => this.getStatusLabel(this.currentStatus()));
  targetStatusLabel = computed(() => this.getStatusLabel(this.targetStatus()));
  currentStatusClass = computed(() => this.getStatusClass(this.currentStatus()));
  targetStatusClass = computed(() => this.getStatusClass(this.targetStatus()));

  private getStatusLabel(status: TaskStatus): string {
    switch (status) {
      case 'completed':
        return 'COMPLETED';
      case 'overdue':
        return 'OVERDUE';
      case 'in-progress':
        return 'IN PROGRESS';
    }
  }

  private getStatusClass(status: TaskStatus): string {
    const base = 'text-xs font-bold uppercase px-2.5 py-1 rounded';
    switch (status) {
      case 'completed':
        return `${base} bg-[#e8f5e9] text-[#2e7d32]`;
      case 'overdue':
        return `${base} bg-red-100 text-red-600`;
      case 'in-progress':
        return `${base} bg-amber-100 text-amber-700`;
    }
  }

  onConfirm(): void {
    this.confirmed.emit();
    this.visible.set(false);
  }
}
