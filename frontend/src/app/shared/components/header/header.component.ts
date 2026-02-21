import { Component, inject, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { BadgeModule } from 'primeng/badge';
import { Menu } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import Keycloak from 'keycloak-js';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationPanelComponent } from '../notification-panel/notification-panel.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    ButtonModule,
    BadgeModule,
    Menu,
    NotificationPanelComponent,
  ],
  encapsulation: ViewEncapsulation.None,
  styles: [`
    .user-dropdown.p-menu {
      border-radius: 12px !important;
      overflow: hidden;
      box-shadow: 0 4px 24px rgba(0,0,0,0.12) !important;
      border: 1px solid #e5e7eb !important;
      min-width: 200px;
      padding: 6px !important;
    }
    .user-dropdown .p-menu-list {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .user-dropdown .p-menu-item {
      border-radius: 8px;
      overflow: hidden;
    }
    .user-dropdown .p-menu-item-content {
      border-radius: 8px;
      transition: background-color 0.15s;
    }
    .user-dropdown .p-menu-item-content:hover {
      background-color: #f3f4f6 !important;
    }
    .user-dropdown .p-menu-item-link {
      padding: 10px 14px !important;
      gap: 10px;
    }
    .user-dropdown .p-menu-item-icon {
      font-size: 1rem;
      color: #6b7280;
    }
    .user-dropdown .p-menu-item-label {
      font-size: 0.875rem;
      font-weight: 500;
      color: #374151;
    }
    .notification-dropdown.p-menu {
      border-radius: 12px !important;
      overflow: hidden;
      box-shadow: 0 4px 24px rgba(0,0,0,0.12) !important;
      border: 1px solid #e5e7eb !important;
      padding: 0 !important;
    }
    .notification-dropdown .p-menu-list {
      padding: 0;
    }
    .services-dropdown.p-menu {
      border-radius: 12px !important;
      overflow: hidden;
      box-shadow: 0 4px 24px rgba(0,0,0,0.12) !important;
      border: 1px solid #e5e7eb !important;
      min-width: 200px;
      padding: 6px !important;
    }
    .services-dropdown .p-menu-list {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .services-dropdown .p-menu-item {
      border-radius: 8px;
      overflow: hidden;
    }
    .services-dropdown .p-menu-item-content {
      border-radius: 8px;
      transition: background-color 0.15s;
    }
    .services-dropdown .p-menu-item-content:hover {
      background-color: #f3f4f6 !important;
    }
    .services-dropdown .p-menu-item-link {
      padding: 10px 14px !important;
      gap: 10px;
    }
    .services-dropdown .p-menu-item-icon {
      font-size: 1rem;
      color: #6b7280;
    }
    .services-dropdown .p-menu-item-label {
      font-size: 0.875rem;
      font-weight: 500;
      color: #374151;
    }
  `],
  template: `
    <header class="flex items-center justify-between px-1 sm:px-6 py-3 bg-[#1b2e35] text-white">
      <!-- Logo -->
      <div class="flex items-center gap-2">
        <img src="assets/todo.svg" alt="Todo Logo" class="w-9 h-9">
        <span class="text-lg font-semibold tracking-wide">TODO</span>
      </div>

      <!-- Right actions -->
      <div class="flex items-center gap-1 sm:gap-2">
        <!-- Notification bell -->
        <div class="relative">
          <button
            class="w-10 h-10 rounded-full bg-[#2a4a4a] flex items-center justify-center text-white hover:bg-[#3a5a5a] transition-colors cursor-pointer border-none"
            (click)="notifOpen = !notifOpen"
          >
            <i class="pi pi-bell text-lg"></i>
          </button>
          @if (notificationService.unreadCount() > 0) {
            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold">
              {{ notificationService.unreadCount() }}
            </span>
          }
          @if (notifOpen) {
            <div class="fixed inset-0 z-40 sm:hidden" (click)="notifOpen = false"></div>
            <div class="fixed inset-0 z-50 sm:absolute sm:inset-auto sm:top-full sm:right-0 sm:mt-2">
              <app-notification-panel (close)="notifOpen = false" />
            </div>
          }
        </div>

        <!-- Services menu -->
        <button
            class="w-10 h-10 rounded-full bg-[#2a4a4a] flex items-center justify-center text-white hover:bg-[#3a5a5a] transition-colors cursor-pointer border-none"
            (click)="servicesMenu.toggle($event)"
        >
          <i class="pi pi-th-large text-lg"></i>
        </button>
        <p-menu #servicesMenu [popup]="true" [model]="servicesMenuItems" styleClass="services-dropdown" />
        
        <!-- User pill -->
        <button
          class="flex items-center gap-2 bg-[#2e7d32] hover:bg-[#388e3c] text-white rounded-full pl-1 pr-4 py-1 transition-colors cursor-pointer border-none"
          (click)="userMenu.toggle($event)"
        >
          @if (userPicture) {
            <img [src]="userPicture" alt="User" class="w-8 h-8 rounded-full object-cover" referrerpolicy="no-referrer">
          } @else {
            <div class="w-8 h-8 rounded-full bg-[#a5d6a7] flex items-center justify-center">
              <i class="pi pi-user text-[#2e7d32]"></i>
            </div>
          }
          <span class="text-sm font-medium">{{ userName }}</span>
        </button>
        <p-menu #userMenu [popup]="true" [model]="userMenuItems" styleClass="user-dropdown" />
      </div>
    </header>
  `,
})
export class HeaderComponent implements OnInit, OnDestroy {
  notificationService = inject(NotificationService);
  private keycloak = inject(Keycloak);
  userName = '';
  userPicture = '';
  userMenuItems: MenuItem[] = [];
  notifOpen = false;
  servicesMenuItems: MenuItem[] = [
    { label: 'Source Code',  icon: 'pi pi-github',     command: () => window.open('https://github.com/hadiubaidillah/todo', '_blank') },
    { label: 'Swagger',      icon: 'pi pi-file',                 command: () => window.open(environment.services.swagger,      '_blank') },
    { label: 'Eureka',       icon: 'pi pi-server',     command: () => window.open(environment.services.eureka,       '_blank') },
    { label: 'Grafana',      icon: 'pi pi-chart-pie',  command: () => window.open(environment.services.grafana,      '_blank') },
    { label: 'Prometheus',   icon: 'pi pi-chart-bar',  command: () => window.open(environment.services.prometheus,   '_blank') },
    { label: 'Zipkin',       icon: 'pi pi-chart-line', command: () => window.open(environment.services.zipkin,       '_blank') },
    { label: 'Alertmanager', icon: 'pi pi-bell',       command: () => window.open(environment.services.alertmanager, '_blank') },
  ];

  ngOnInit(): void {
    // Initialize notifications with proper sequencing to prevent race conditions:
    // 1. Load initial data (notifications + unread count)
    // 2. Only then connect SSE for real-time updates
    this.notificationService.initialize();

    this.keycloak.loadUserProfile().then((profile) => {
      console.log('profile: ', profile)
      this.userName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim() || profile.username || '';
      this.userPicture = (profile as any).attributes?.picture?.[0] || '';
      this.userMenuItems = [
        {
          label: this.userName,
          icon: 'pi pi-user',
          disabled: true,
        },
        {
          label: 'Manage Account',
          icon: 'pi pi-cog',
          command: () => window.open(this.keycloak.createAccountUrl(), '_blank'),
        },
        {
          label: 'Logout',
          icon: 'pi pi-sign-out',
          command: () => this.logout(),
        },
      ];
    });
  }

  ngOnDestroy(): void {
    this.notificationService.disconnectSSE();
  }

  logout(): void {
    this.keycloak.logout();
  }
}
