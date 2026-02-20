export const environment = {
  production: true,
  keycloakUrl: (window as any).__env?.KEYCLOAK_URL || 'http://localhost:8083',
  keycloakRealm: (window as any).__env?.KEYCLOAK_REALM || 'todo',
  keycloakClientId: (window as any).__env?.KEYCLOAK_CLIENT_ID || 'todo-angular',
  apiGatewayUrl: (window as any).__env?.API_GATEWAY_URL || 'http://localhost:8080/api/v1',
  services: {
    swagger:      ((window as any).__env?.API_GATEWAY_URL || 'http://localhost:8080/api/v1').replace('/api/v1', '') + '/swagger-ui/index.html',
    eureka:       (window as any).__env?.EUREKA_URL       || 'https://eureka.hadiubaidillah.com',
    grafana:      (window as any).__env?.GRAFANA_URL      || 'https://grafana.hadiubaidillah.com',
    prometheus:   (window as any).__env?.PROMETHEUS_URL   || 'https://prometheus.hadiubaidillah.com',
    zipkin:       (window as any).__env?.ZIPKIN_URL       || 'https://zipkin.hadiubaidillah.com',
    alertmanager: (window as any).__env?.ALERTMANAGER_URL || 'https://alertmanager.hadiubaidillah.com',
  },
};
