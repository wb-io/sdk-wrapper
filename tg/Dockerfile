FROM nginxinc/nginx-unprivileged:1.24

WORKDIR /app
COPY ./nginx /etc/nginx
COPY v1 /app/v1

EXPOSE 8080
