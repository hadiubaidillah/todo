import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import Keycloak from 'keycloak-js';

export const authGuard: CanActivateFn = async () => {
  const keycloak = inject(Keycloak);

  if (!keycloak.authenticated) {
    await keycloak.login();
    return false;
  }
  return true;
};
