import { Component, effect, inject, input, model, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dialog } from 'primeng/dialog';
import { DatePicker } from 'primeng/datepicker';
import { Task, TaskDTO } from '../../models/task.model';

@Component({
  selector: 'app-edit-task-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    Dialog,
    DatePicker,
  ],
  template: `
    <p-dialog
      header="Edit task"
      [(visible)]="visible"
      [modal]="true"
      [style]="{ width: '480px' }"
      [closable]="true"
      (onHide)="onCancel()"
    >
      <form [formGroup]="form" class="flex flex-col gap-5">
        <div class="flex flex-col gap-1.5">
          <label for="edit-title" class="text-sm font-medium text-surface-700">Title</label>
          <input
            id="edit-title"
            type="text"
            formControlName="name"
            placeholder="Task title"
            class="w-full px-3 py-2.5 rounded-lg border border-surface-300 bg-surface-50 text-sm outline-none focus:border-[#2e7d32] focus:ring-1 focus:ring-[#2e7d32] transition-colors"
          />
          @if (form.controls.name.touched && form.controls.name.hasError('required')) {
            <small class="text-red-500 text-xs">Title is required</small>
          }
        </div>

        <div class="flex flex-col gap-1.5">
          <label for="edit-description" class="text-sm font-medium text-surface-700">Description</label>
          <textarea
            id="edit-description"
            formControlName="description"
            rows="4"
            placeholder="Task description"
            class="w-full px-3 py-2.5 rounded-lg border border-surface-300 bg-white text-sm outline-none focus:border-[#2e7d32] focus:ring-1 focus:ring-[#2e7d32] transition-colors resize-y"
          ></textarea>
        </div>

        <div class="flex flex-col gap-1.5">
          <label for="edit-endsIn" class="text-sm font-medium text-surface-700">End date</label>
          <p-datepicker
            formControlName="endsIn"
            [showTime]="true"
            [showIcon]="true"
            [appendTo]="'body'"
            dateFormat="mm/dd/yy"
            placeholder="Select date and time"
            styleClass="w-full"
          />
        </div>
      </form>

      <ng-template #footer>
        <div class="flex justify-end gap-3">
          <button
            class="px-5 py-2.5 rounded-lg bg-surface-300 hover:bg-surface-400 text-surface-700 text-sm font-medium cursor-pointer border-none transition-colors"
            (click)="onCancel()"
          >
            Cancel
          </button>
          <button
            class="px-5 py-2.5 rounded-lg bg-[#0288d1] hover:bg-[#0277bd] text-white text-sm font-medium cursor-pointer border-none transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            [disabled]="form.invalid"
            (click)="submit()"
          >
            Save
          </button>
        </div>
      </ng-template>
    </p-dialog>
  `,
})
export class EditTaskDialogComponent {
  private fb = inject(FormBuilder);

  visible = model(false);
  task = input<Task | null>(null);
  taskEdited = output<TaskDTO>();

  form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: [''],
    endsIn: [null as Date | null],
  });

  constructor() {
    effect(() => {
      const t = this.task();
      if (t) {
        this.form.patchValue({
          name: t.name,
          description: t.description ?? '',
          endsIn: t.endsIn ? new Date(t.endsIn) : null,
        });
      }
    });
  }

  submit(): void {
    if (this.form.valid) {
      const value = this.form.value;
      const dto: TaskDTO = {
        name: value.name!,
        description: value.description || undefined,
        endsIn: value.endsIn ? new Date(value.endsIn).toISOString() : undefined,
      };
      this.taskEdited.emit(dto);
      this.visible.set(false);
      this.form.reset();
    }
  }

  onCancel(): void {
    this.visible.set(false);
    this.form.reset();
  }
}
