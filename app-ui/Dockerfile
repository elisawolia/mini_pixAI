FROM node:17-alpine as builder
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN npm build

EXPOSE 8087
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/build .
ENTRYPOINT ["nginx", "-g", "daemon off;"]

FROM nginx:1.19.0

EXPOSE 8087
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/build .
ENTRYPOINT ["nginx", "-g", "daemon off;"]