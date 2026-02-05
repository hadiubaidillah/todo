import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinner } from 'primeng/progressspinner';
import { InputTextModule } from 'primeng/inputtext';
import { Subscription } from 'rxjs';
import { TaskService } from '../../services/task.service';
import { TaskCardComponent } from '../task-card/task-card.component';
import { CreateTaskDialogComponent } from '../create-task-dialog/create-task-dialog.component';
import { DeleteTaskDialogComponent } from '../delete-task-dialog/delete-task-dialog.component';
import { EditTaskDialogComponent } from '../edit-task-dialog/edit-task-dialog.component';
import { ToggleStatusDialogComponent } from '../toggle-status-dialog/toggle-status-dialog.component';
import { NotificationService } from '../../../../core/services/notification.service';
import { Task, TaskDTO } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    ProgressSpinner,
    InputTextModule,
    TaskCardComponent,
    CreateTaskDialogComponent,
    DeleteTaskDialogComponent,
    EditTaskDialogComponent,
    ToggleStatusDialogComponent,
  ],
  template: `
    <div class="max-w-4xl mx-auto px-6 py-8">
      <h1 class="text-3xl font-bold text-surface-800 mb-8">To Do List</h1>

      <!-- Search bar + Add button -->
      <div class="flex items-center gap-3 mb-8 mx-auto">
        <div class="flex-1 flex items-center bg-white rounded-lg shadow-sm border border-surface-200 px-4 py-2">
          <i class="pi pi-search text-surface-400 mr-3"></i>
          <input
            type="text"
            placeholder="Search for a task"
            class="flex-1 border-none outline-none bg-transparent text-sm"
            [(ngModel)]="searchQuery"
          />
        </div>
        <button
          class="w-10 h-10 rounded-lg bg-[#2e7d32] hover:bg-[#388e3c] text-white flex items-center justify-center cursor-pointer border-none shadow-sm transition-colors"
          (click)="showCreateDialog.set(true)"
        >
          <i class="pi pi-plus text-lg font-bold"></i>
        </button>
      </div>

      @if (taskService.loading()) {
        <div class="flex justify-center py-16">
          <p-progressSpinner [style]="{ width: '40px', height: '40px' }" />
        </div>
      } @else if (filteredTasks().length === 0) {
        <div class="text-center py-16 text-surface-400">
          <i class="pi pi-check-circle text-6xl mb-4 block"></i>
          <p class="text-lg">No tasks yet. Create your first task!</p>
        </div>
      } @else {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mx-auto">
          @for (task of filteredTasks(); track task.id) {
            <app-task-card
              [task]="task"
              (edit)="onEdit($event)"
              (toggle)="onToggle($event)"
              (delete)="onDelete($event)"
            />
          }
        </div>
      }

      <app-create-task-dialog
        [(visible)]="showCreateDialog"
        (taskCreated)="onTaskCreated($event)"
      />
      <app-delete-task-dialog
        [(visible)]="showDeleteDialog"
        [taskName]="deleteTaskName()"
        (confirmed)="onDeleteConfirmed()"
      />
      <app-edit-task-dialog
        [(visible)]="showEditDialog"
        [task]="editTask()"
        (taskEdited)="onTaskEdited($event)"
      />
      <app-toggle-status-dialog
        [(visible)]="showToggleDialog"
        [task]="toggleTask()"
        (confirmed)="onToggleConfirmed()"
      />
    </div>
  `,
})
export class TaskListComponent implements OnInit, OnDestroy {
  taskService = inject(TaskService);
  private notificationService = inject(NotificationService);

  showCreateDialog = signal(false);
  showDeleteDialog = signal(false);
  showEditDialog = signal(false);
  showToggleDialog = signal(false);
  editTask = signal<Task | null>(null);
  toggleTask = signal<Task | null>(null);
  deleteTaskId = signal('');
  deleteTaskName = signal('');
  searchQuery = '';

  private refreshInterval: ReturnType<typeof setInterval> | null = null;
  private statusChangeSubscription: Subscription | null = null;

  ngOnInit(): void {
    this.taskService.loadTasks();

    // Auto-refresh every 30 seconds to update time-based statuses (IN_PROGRESS → OVERDUE)
    this.refreshInterval = setInterval(() => {
      this.taskService.triggerRefresh();
    }, 30000);

    // Listen for task status changes from backend (via SSE notifications)
    this.statusChangeSubscription = this.notificationService.onTaskStatusChange.subscribe(() => {
      console.log('[TaskList] Task status changed, reloading tasks...');
      this.taskService.loadTasks();
    });
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
      this.refreshInterval = null;
    }
    if (this.statusChangeSubscription) {
      this.statusChangeSubscription.unsubscribe();
      this.statusChangeSubscription = null;
    }
  }

  filteredTasks() {
    const query = this.searchQuery.toLowerCase().trim();
    if (!query) return this.taskService.tasks();
    return this.taskService.tasks().filter(
      (t) =>
        t.name.toLowerCase().includes(query) ||
        (t.description && t.description.toLowerCase().includes(query))
    );
  }

  onTaskCreated(dto: TaskDTO): void {
    this.taskService.createTask(dto);
  }

  onToggle(id: string): void {
    const task = this.taskService.tasks().find((t) => t.id === id);
    if (!task) return;

    this.toggleTask.set(task);
    this.showToggleDialog.set(true);
  }

  onToggleConfirmed(): void {
    const task = this.toggleTask();
    if (!task) return;
    this.taskService.toggleComplete(task.id);
  }

  onDelete(id: string): void {
    const task = this.taskService.tasks().find((t) => t.id === id);
    if (!task) return;

    this.deleteTaskId.set(id);
    this.deleteTaskName.set(task.name);
    this.showDeleteDialog.set(true);
  }

  onDeleteConfirmed(): void {
    this.taskService.deleteTask(this.deleteTaskId());
  }

  onEdit(id: string): void {
    const task = this.taskService.tasks().find((t) => t.id === id);
    if (!task) return;

    this.editTask.set(task);
    this.showEditDialog.set(true);
  }

  onTaskEdited(dto: TaskDTO): void {
    const task = this.editTask();
    if (!task) return;
    this.taskService.updateTask(task.id, dto);
  }
}
