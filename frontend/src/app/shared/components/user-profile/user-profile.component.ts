import { Component, inject, OnInit } from '@angular/core';
import { Card } from 'primeng/card';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [Card],
  template: `
    <p-card>
      <div class="profile-header">
        <i class="pi pi-user profile-avatar"></i>
        <div>
          <div class="profile-name">{{ fullName }}</div>
          <div class="profile-email">{{ email }}</div>
        </div>
      </div>
    </p-card>
  `,
  styles: [`
    .profile-header {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .profile-avatar {
      font-size: 2rem;
      color: #888;
    }
    .profile-name {
      font-weight: 500;
      font-size: 16px;
    }
    .profile-email {
      color: #666;
      font-size: 14px;
    }
  `],
})
export class UserProfileComponent implements OnInit {
  private keycloak = inject(Keycloak);
  fullName = '';
  email = '';

  ngOnInit(): void {
    this.keycloak.loadUserProfile().then((profile) => {
      this.fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
      this.email = profile.email || '';
    });
  }
}
