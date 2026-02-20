export const environment = {
  production: false,
  keycloakUrl: (window as any).__env?.KEYCLOAK_URL || 'http://localhost:8083',
  keycloakRealm: (window as any).__env?.KEYCLOAK_REALM || 'todo',
  keycloakClientId: (window as any).__env?.KEYCLOAK_CLIENT_ID || 'todo-angular',
  apiGatewayUrl: (window as any).__env?.API_GATEWAY_URL || 'http://localhost:8080/api/v1',
  services: {
    swagger:      ((window as any).__env?.API_GATEWAY_URL || 'http://localhost:8080/api/v1').replace('/api/v1', '') + '/swagger-ui/index.html',
    eureka:       (window as any).__env?.EUREKA_URL       || 'http://localhost:8761',
    grafana:      (window as any).__env?.GRAFANA_URL      || 'http://localhost:3000',
    prometheus:   (window as any).__env?.PROMETHEUS_URL   || 'http://localhost:9090',
    zipkin:       (window as any).__env?.ZIPKIN_URL       || 'http://localhost:9411',
    alertmanager: (window as any).__env?.ALERTMANAGER_URL || 'http://localhost:9093',
  },
};
