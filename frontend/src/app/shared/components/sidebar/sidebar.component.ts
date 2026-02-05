import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="flex flex-col py-2">
      <a
        routerLink="/todo"
        routerLinkActive="bg-primary/10 text-primary font-semibold"
        class="flex items-center gap-3 px-4 py-3 text-sm text-surface-700 no-underline hover:bg-surface-100 transition-colors"
      >
        <i class="pi pi-check-square text-lg"></i>
        <span>Todo</span>
      </a>
    </nav>
  `,
})
export class SidebarComponent {}
