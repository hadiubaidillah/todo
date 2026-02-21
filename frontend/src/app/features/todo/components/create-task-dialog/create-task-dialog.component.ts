import { Component, inject, model, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dialog } from 'primeng/dialog';
import { DatePicker } from 'primeng/datepicker';
import { ProgressSpinner } from 'primeng/progressspinner';
import { TaskDTO } from '../../models/task.model';
import { AiTaskService } from '../../services/ai-task.service';

@Component({
  selector: 'app-create-task-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    Dialog,
    DatePicker,
    ProgressSpinner,
  ],
  template: `
    <p-dialog
      header="Create a task"
      [(visible)]="visible"
      [modal]="true"
      [style]="{ width: '500px' }"
      [closable]="true"
      (onHide)="onCancel()"
    >
      <!-- Mode Toggle -->
      <div class="flex gap-1 mb-5 bg-surface-100 p-1 rounded-lg">
        <button
          class="flex-1 py-2 text-sm font-medium rounded-md transition-colors cursor-pointer border-none"
          [class]="mode() === 'manual'
            ? 'bg-white text-surface-800 shadow-sm'
            : 'bg-transparent text-surface-500 hover:text-surface-700'"
          (click)="setMode('manual')"
        >
          Manual
        </button>
        <button
          class="flex-1 py-2 text-sm font-medium rounded-md transition-colors cursor-pointer border-none"
          [class]="mode() === 'ai'
            ? 'bg-white text-surface-800 shadow-sm'
            : 'bg-transparent text-surface-500 hover:text-surface-700'"
          (click)="setMode('ai')"
        >
          ✨ AI
        </button>
      </div>

      @if (mode() === 'manual') {
        <form [formGroup]="form" class="flex flex-col gap-5">
          <div class="flex flex-col gap-1.5">
            <label for="title" class="text-sm font-medium text-surface-700">Title</label>
            <input
              id="title"
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
            <label for="description" class="text-sm font-medium text-surface-700">Description</label>
            <textarea
              id="description"
              formControlName="description"
              rows="4"
              placeholder="Task description"
              class="w-full px-3 py-2.5 rounded-lg border border-surface-300 bg-white text-sm outline-none focus:border-[#2e7d32] focus:ring-1 focus:ring-[#2e7d32] transition-colors resize-y"
            ></textarea>
          </div>

          <div class="flex flex-col gap-1.5">
            <label for="endsIn" class="text-sm font-medium text-surface-700">End date</label>
            <p-datepicker
              formControlName="endsIn"
              [showTime]="true"
              [showIcon]="true"
              [appendTo]="'body'"
              [minDate]="today"
              dateFormat="mm/dd/yy"
              placeholder="Select date and time"
              styleClass="w-full"
            />
          </div>
        </form>
      } @else {
        <div class="flex flex-col gap-4">
          <div class="flex flex-col gap-1.5">
            <label class="text-sm font-medium text-surface-700">Describe your tasks</label>
            <textarea
              rows="4"
              placeholder="E.g. Meeting with team tomorrow at 2pm, then send notes to manager by Friday..."
              class="w-full px-3 py-2.5 rounded-lg border border-surface-300 bg-white text-sm outline-none focus:border-[#2e7d32] focus:ring-1 focus:ring-[#2e7d32] transition-colors resize-y"
              [value]="aiInput()"
              (input)="aiInput.set($any($event.target).value)"
            ></textarea>
          </div>

          <button
            class="w-full py-2.5 rounded-lg bg-[#2e7d32] hover:bg-[#388e3c] text-white text-sm font-medium cursor-pointer border-none transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            [disabled]="isParsing() || !aiInput().trim()"
            (click)="parseTasks()"
          >
            @if (isParsing()) {
              <p-progressSpinner [style]="{ width: '16px', height: '16px' }" styleClass="!w-4 !h-4" />
              Parsing...
            } @else {
              ✨ Parse Tasks
            }
          </button>

          @if (parseError()) {
            <small class="text-red-500 text-xs">{{ parseError() }}</small>
          }

          @if (parsedTasks().length > 0) {
            <div class="flex flex-col gap-2">
              <p class="text-sm font-medium text-surface-700">
                {{ parsedTasks().length }} task{{ parsedTasks().length > 1 ? 's' : '' }} found:
              </p>
              @for (task of parsedTasks(); track $index) {
                <div class="flex items-start justify-between gap-3 p-3 rounded-lg border border-surface-200 bg-surface-50">
                  <div class="flex flex-col gap-0.5 min-w-0">
                    <p class="text-sm font-medium text-surface-800 truncate">{{ task.name }}</p>
                    @if (task.description) {
                      <p class="text-xs text-surface-500 truncate">{{ task.description }}</p>
                    }
                    @if (task.endsIn) {
                      <p class="text-xs text-[#0288d1]">📅 {{ formatDate(task.endsIn) }}</p>
                    }
                  </div>
                  <button
                    class="flex-shrink-0 w-6 h-6 flex items-center justify-center rounded text-surface-400 hover:text-red-500 hover:bg-red-50 cursor-pointer border-none bg-transparent transition-colors"
                    (click)="removeParsedTask($index)"
                    title="Remove task"
                  >
                    <i class="pi pi-times text-xs"></i>
                  </button>
                </div>
              }
            </div>
          }
        </div>
      }

      <ng-template #footer>
        <div class="flex justify-end gap-3">
          <button
            class="px-5 py-2.5 rounded-lg bg-surface-300 hover:bg-surface-400 text-surface-700 text-sm font-medium cursor-pointer border-none transition-colors"
            (click)="onCancel()"
          >
            Cancel
          </button>
          @if (mode() === 'manual') {
            <button
              class="px-5 py-2.5 rounded-lg bg-[#0288d1] hover:bg-[#0277bd] text-white text-sm font-medium cursor-pointer border-none transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              [disabled]="form.invalid"
              (click)="submit()"
            >
              Create
            </button>
          } @else if (parsedTasks().length > 0) {
            <button
              class="px-5 py-2.5 rounded-lg bg-[#0288d1] hover:bg-[#0277bd] text-white text-sm font-medium cursor-pointer border-none transition-colors"
              (click)="createAiTasks()"
            >
              Create {{ parsedTasks().length }} Task{{ parsedTasks().length > 1 ? 's' : '' }}
            </button>
          }
        </div>
      </ng-template>
    </p-dialog>
  `,
})
export class CreateTaskDialogComponent {
  private fb = inject(FormBuilder);
  private aiTaskService = inject(AiTaskService);

  visible = model(false);
  taskCreated = output<TaskDTO>();
  tasksCreated = output<TaskDTO[]>();

  today = new Date();
  mode = signal<'manual' | 'ai'>('manual');
  aiInput = signal('');
  parsedTasks = signal<TaskDTO[]>([]);
  isParsing = signal(false);
  parseError = signal('');

  form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: [''],
    endsIn: [null as Date | null],
  });

  setMode(m: 'manual' | 'ai'): void {
    this.mode.set(m);
    if (m === 'ai') {
      this.parsedTasks.set([]);
      this.parseError.set('');
    }
  }

  parseTasks(): void {
    const text = this.aiInput();
    if (!text.trim()) return;

    this.isParsing.set(true);
    this.parseError.set('');

    this.aiTaskService.parseTasks(text).subscribe({
      next: (tasks) => {
        this.parsedTasks.set(tasks);
        this.isParsing.set(false);
      },
      error: () => {
        this.parseError.set('Failed to parse tasks. Please check your input and try again.');
        this.isParsing.set(false);
      },
    });
  }

  removeParsedTask(index: number): void {
    this.parsedTasks.update((tasks) => tasks.filter((_, i) => i !== index));
  }

  createAiTasks(): void {
    const tasks = this.parsedTasks();
    if (tasks.length === 0) return;
    this.tasksCreated.emit(tasks);
    this.visible.set(false);
    this.reset();
  }

  formatDate(isoString: string): string {
    return new Date(isoString).toLocaleString(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short',
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
      this.taskCreated.emit(dto);
      this.visible.set(false);
      this.reset();
    }
  }

  onCancel(): void {
    this.visible.set(false);
    this.reset();
  }

  private reset(): void {
    this.form.reset();
    this.mode.set('manual');
    this.aiInput.set('');
    this.parsedTasks.set([]);
    this.parseError.set('');
  }
}
