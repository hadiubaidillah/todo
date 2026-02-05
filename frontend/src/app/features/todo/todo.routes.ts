import { Routes } from '@angular/router';
import { TaskListComponent } from './components/task-list/task-list.component';

export const todoRoutes: Routes = [
  { path: '', component: TaskListComponent },
];
