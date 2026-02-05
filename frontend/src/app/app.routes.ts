import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'todo', pathMatch: 'full' },
      {
        path: 'todo',
        loadChildren: () =>
          import('./features/todo/todo.routes').then((m) => m.todoRoutes),
      },
    ],
  },
];
