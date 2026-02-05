import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { from, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);

  if (!req.url.startsWith(environment.apiGatewayUrl)) {
    return next(req);
  }

  return from(keycloak.getToken()).pipe(
    switchMap((token) => {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      return next(authReq);
    })
  );
};
