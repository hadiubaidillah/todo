FROM node:22-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production

FROM nginx:alpine
COPY --from=builder /app/dist/microservices-portal/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh && mkdir -p /usr/share/nginx/html/assets
EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
