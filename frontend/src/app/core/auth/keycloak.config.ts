import { environment } from '../../../environments/environment';

export const keycloakConfig = {
  url: environment.keycloakUrl,
  realm: environment.keycloakRealm,
  clientId: environment.keycloakClientId,
};
