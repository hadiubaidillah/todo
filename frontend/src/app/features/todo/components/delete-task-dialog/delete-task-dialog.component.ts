import { Component, input, model, output } from '@angular/core';
import { Dialog } from 'primeng/dialog';

@Component({
  selector: 'app-delete-task-dialog',
  standalone: true,
  imports: [Dialog],
  template: `
    <p-dialog
      header="Delete Task"
      [(visible)]="visible"
      [modal]="true"
      [style]="{ width: '400px' }"
      styleClass="!rounded-xl"
    >
      <p class="text-sm text-surface-600 m-0">
        Are you sure you want to delete <strong>"{{ taskName() }}"</strong>?
      </p>

      <ng-template #footer>
        <div class="flex justify-end gap-3">
          <button
            class="px-5 py-2.5 rounded-lg bg-surface-300 hover:bg-surface-400 text-surface-700 text-sm font-medium cursor-pointer border-none transition-colors"
            (click)="visible.set(false)"
          >
            Cancel
          </button>
          <button
            class="px-5 py-2.5 rounded-lg bg-red-500 hover:bg-red-600 text-white text-sm font-medium cursor-pointer border-none transition-colors"
            (click)="onConfirm()"
          >
            Delete
          </button>
        </div>
      </ng-template>
    </p-dialog>
  `,
})
export class DeleteTaskDialogComponent {
  visible = model(false);
  taskName = input('');
  confirmed = output<void>();

  onConfirm(): void {
    this.confirmed.emit();
    this.visible.set(false);
  }
}
