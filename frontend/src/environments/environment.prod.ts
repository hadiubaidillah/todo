export const environment = {
  production: true,
  keycloakUrl: (window as any).__env?.KEYCLOAK_URL || 'http://localhost:8083',
  keycloakRealm: (window as any).__env?.KEYCLOAK_REALM || 'todo',
  keycloakClientId: (window as any).__env?.KEYCLOAK_CLIENT_ID || 'todo-angular',
  apiGatewayUrl: (window as any).__env?.API_GATEWAY_URL || 'http://localhost:8080/api/v1',
};
