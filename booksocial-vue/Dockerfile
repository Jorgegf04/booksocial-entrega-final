# Dockerfile del frontend Vue.
# Crea la imagen de la aplicación Vue para ejecutarla en un contenedor.
# Primero compila el proyecto con Node y después usa Nginx para servir los archivos generados en el puerto 80.
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci --prefer-offline

COPY . .

ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

FROM nginx:alpine
WORKDIR /usr/share/nginx/html

COPY --from=build /app/dist .

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
