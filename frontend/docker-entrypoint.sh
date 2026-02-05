#!/bin/sh

# Runtime env injection for Angular
cat <<EOF > /usr/share/nginx/html/assets/env.js
(function(window) {
  window.__env = {
    KEYCLOAK_URL: '${KEYCLOAK_URL}',
    KEYCLOAK_REALM: '${KEYCLOAK_REALM}',
    KEYCLOAK_CLIENT_ID: '${KEYCLOAK_CLIENT_ID}',
    API_GATEWAY_URL: '${API_GATEWAY_URL}'
  };
})(this);
EOF

exec nginx -g 'daemon off;'
