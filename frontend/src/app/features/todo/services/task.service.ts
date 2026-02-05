import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Task, TaskDTO } from '../models/task.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly baseUrl = `${environment.apiGatewayUrl}/tasks`;

  tasks = signal<Task[]>([]);
  loading = signal<boolean>(false);

  // Signal to trigger re-render for time-based status updates
  private refreshTrigger = signal<number>(0);

  constructor(private http: HttpClient) {}

  /**
   * Force re-render of task list to update time-based statuses (e.g., IN_PROGRESS → OVERDUE)
   */
  triggerRefresh(): void {
    this.refreshTrigger.update(v => v + 1);
    // Force signal update by creating new array reference
    this.tasks.update(list => [...list]);
  }

  getRefreshTrigger() {
    return this.refreshTrigger;
  }

  loadTasks(): void {
    this.loading.set(true);
    this.http.get<Task[]>(this.baseUrl).subscribe({
      next: (data) => {
        this.tasks.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  createTask(dto: TaskDTO): void {
    this.http.post<Task>(this.baseUrl, dto).subscribe({
      next: (task) => {
        this.tasks.update((list) => [task, ...list]);
      },
    });
  }

  updateTask(id: string, dto: TaskDTO): void {
    this.http.put<Task>(`${this.baseUrl}/${id}`, dto).subscribe({
      next: (updated) => {
        this.tasks.update((list) =>
          list.map((t) => (t.id === id ? updated : t))
        );
      },
    });
  }

  toggleComplete(id: string): void {
    this.http.patch<Task>(`${this.baseUrl}/${id}/toggle`, {}).subscribe({
      next: (updated) => {
        this.tasks.update((list) =>
          list.map((t) => (t.id === id ? updated : t))
        );
      },
    });
  }

  deleteTask(id: string): void {
    this.http.delete(`${this.baseUrl}/${id}`).subscribe({
      next: () => {
        this.tasks.update((list) => list.filter((t) => t.id !== id));
      },
    });
  }
}
