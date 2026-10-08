FROM m.daocloud.io/docker.io/library/node:22-alpine AS build

WORKDIR /app


COPY \
  sxupr-ui-admin-vue3/package*.json \
  ./


RUN npm ci


COPY \
  sxupr-ui-admin-vue3/ \
  ./


RUN npm run build



FROM m.daocloud.io/docker.io/library/nginx:alpine


COPY \
  deploy/nginx/default.conf \
  /etc/nginx/conf.d/default.conf


COPY \
  --from=build \
  /app/dist \
  /usr/share/nginx/html