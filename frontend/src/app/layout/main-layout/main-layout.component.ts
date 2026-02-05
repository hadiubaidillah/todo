import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    HeaderComponent,
  ],
  template: `
    <div class="flex flex-col h-screen">
      <app-header />
      <main class="flex-1 overflow-y-auto bg-[#e8edf2]">
        <router-outlet />
      </main>
    </div>
  `,
})
export class MainLayoutComponent {}
