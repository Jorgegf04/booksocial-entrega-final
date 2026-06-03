# BookSocial

BookSocial es un proyecto dividido en varias aplicaciones:

- Un backend con Spring Boot que expone la API REST.
- Un frontend publico con Spring Boot + Thymeleaf.
- Un panel de administracion con Vue.
- Una base de datos MySQL levantada con Docker.

## Estructura del proyecto

| Carpeta / archivo | Para que sirve |
| --- | --- |
| `booksocial-backend/booksocial-backend` | Backend principal. Contiene la API REST, entidades, servicios, repositorios y seguridad. |
| `booksocial-frontend` | Frontend publico hecho con Spring Boot y Thymeleaf. |
| `booksocial-vue` | Panel de administracion hecho con Vue. |
| `data` | Datos locales de base de datos usados durante el desarrollo. |
| `docs` | Documentacion y recursos del proyecto. |
| `scripts` | Scripts y colecciones auxiliares. |
| `uploads` | Archivos o imagenes subidas por la aplicacion. |
| `docker-compose.yml` | Configuracion para levantar toda la aplicacion con Docker. |

## Arranque con Docker

Desde la raiz del proyecto:

```bash
cp .env.example .env
```

Si se quiere probar el envio de correo, rellena `MAIL_USERNAME` y `MAIL_PASSWORD` en `.env`. Para una prueba normal de la aplicacion se pueden dejar vacios.

```bash
docker compose up -d --build
```

Este comando construye y arranca todos los servicios. La primera vez puede tardar unos minutos porque descarga imagenes y compila los proyectos.

Si existe un volcado en `data/mysql-init/01-booksocial.sql`, el servicio `mysql-seed` lo importa automaticamente durante el arranque cuando la base esta vacia. Las imagenes subidas por la aplicacion se guardan en `uploads`, por lo que esa carpeta debe viajar junto al proyecto si se quiere conservar el mismo contenido.

Para comprobar que todo esta funcionando:

```bash
docker compose ps
```

Para parar los contenedores sin borrar datos:

```bash
docker compose down
```

Para parar y borrar tambien la base de datos guardada en volumen:

```bash
docker compose down -v
```

Despues de `docker compose down -v`, al volver a ejecutar `docker compose up -d --build`, MySQL reimportara `data/mysql-init/01-booksocial.sql` si existe.

## Exportar datos para otra maquina

En el ordenador que ya tiene los datos cargados, con los contenedores arrancados, ejecuta:

```powershell
.\scripts\export-delivery-data.ps1
```

Esto genera:

| Ruta | Contenido |
| --- | --- |
| `data/mysql-init/01-booksocial.sql` | Volcado completo de la base de datos MySQL. |
| `uploads` | Imagenes subidas mediante la aplicacion. |

Despues sube esos cambios a GitHub o incluyelos en el ZIP de entrega. En otro ordenador, si la base esta vacia, Docker importara el SQL automaticamente durante `docker compose up --build`.

Si el otro ordenador ya habia creado el volumen de MySQL antes de tener el SQL, ejecuta:

```bash
docker compose down -v
docker compose up -d --build
```

## Servicios del docker-compose

| Servicio | Contenedor | Puerto / acceso | Descripcion |
| --- | --- | --- | --- |
| MySQL | `mysql-db` | Solo interno | Base de datos `booksocial`. No se expone al ordenador directamente. |
| Backend API | `backend-api` | Solo interno, puerto `9999` | API REST usada por los frontends. |
| Frontend Thymeleaf | `frontend-thymeleaf` | Publicado por Nginx en `http://localhost` | Aplicacion web publica. |
| Nginx Thymeleaf | `nginx-thymeleaf` | `80` y `443` | Proxy para abrir el frontend Thymeleaf desde el navegador. |
| Frontend Vue | `frontend-vue` | `http://localhost:8080` | Panel de administracion. Tambien reenvia `/api` al backend. |

El backend y MySQL estan en una red privada de Docker. Por eso no se abren directamente desde el navegador. Se accede a ellos a traves de los frontends.

## Rutas para abrir la aplicacion

| URL | Aplicacion |
| --- | --- |
| `http://localhost` | Frontend publico Thymeleaf |
| `https://localhost` | Frontend publico Thymeleaf con HTTPS local |
| `http://localhost:8080` | Panel de administracion Vue |
| `http://localhost:8080/api/...` | API del backend pasando por el proxy de Vue |

## Rutas principales del frontend publico

Estas rutas se abren desde `http://localhost`:

| Ruta | Pantalla |
| --- | --- |
| `/` | Inicio |
| `/catalog` | Catalogo de obras |
| `/work/{id}` | Detalle de una obra |
| `/authors` | Listado de autores |
| `/author/{id}` | Detalle de un autor |
| `/community` | Comunidad |
| `/events` | Eventos |
| `/cart` | Carrito |
| `/orders` | Pedidos del usuario |
| `/user/me` | Perfil del usuario actual |
| `/user/{id}` | Perfil de un usuario |
| `/subscription/premium` | Suscripcion premium |
| `/auth/login` | Inicio de sesion |
| `/auth/register` | Registro |
| `/auth/logout` | Cerrar sesion |
| `/admin` | Panel de administracion Thymeleaf |

## Rutas principales del panel Vue

Estas rutas se abren desde `http://localhost:8080`:

| Ruta | Pantalla |
| --- | --- |
| `/login` | Login de administrador |
| `/admin/dashboard` | Dashboard |
| `/admin/obras` | Obras |
| `/admin/autores` | Autores |
| `/admin/editoriales` | Editoriales |
| `/admin/ediciones` | Ediciones |
| `/admin/tomos` | Tomos |
| `/admin/capitulos` | Capitulos |
| `/admin/volumenes` | Volumenes |
| `/admin/inventario` | Inventario |
| `/admin/pedidos` | Pedidos |
| `/admin/usuarios` | Usuarios |
| `/admin/comentarios` | Comentarios |
| `/admin/eventos` | Eventos |

## Rutas principales de la API

En Docker, la API no se abre directamente por `localhost:9999`. Para probarla desde el navegador, Postman o scripts, usa:

```text
http://localhost:8080/api/...
```

Endpoints principales:

| Ruta | Recurso |
| --- | --- |
| `/api/auth/login` | Login |
| `/api/auth/register` | Registro |
| `/api/users` | Usuarios |
| `/api/authors` | Autores |
| `/api/works` | Obras |
| `/api/editorials` | Editoriales |
| `/api/editions` | Ediciones |
| `/api/tomes` | Tomos |
| `/api/chapters` | Capitulos |
| `/api/volumes` | Volumenes |
| `/api/products` | Productos / inventario |
| `/api/orders` | Pedidos |
| `/api/order-lines` | Lineas de pedido |
| `/api/comments` | Comentarios |
| `/api/reactions` | Reacciones |
| `/api/events` | Eventos |
| `/api/subscriptions` | Suscripciones |
| `/api/tracking-works` | Seguimiento de obras |
| `/api/tracking-orders` | Seguimiento de pedidos |
| `/api/upload` | Subida de archivos |

## Ejecutar sin Docker

Tambien se pueden arrancar las aplicaciones por separado:

### Backend

```bash
cd booksocial-backend/booksocial-backend
mvn spring-boot:run
```

URL local:

```text
http://localhost:9999
```

### Frontend Thymeleaf

```bash
cd booksocial-frontend
mvn spring-boot:run
```

URL local:

```text
http://localhost:8000
```

### Frontend Vue

```bash
cd booksocial-vue
npm install
npm run dev
```

URL local habitual:

```text
http://localhost:5173
```

## Comandos utiles

Ver logs de todos los servicios:

```bash
docker compose logs -f
```

Ver logs de un servicio concreto:

```bash
docker compose logs -f backend-api
docker compose logs -f frontend-thymeleaf
docker compose logs -f frontend-vue
docker compose logs -f mysql-db
```

Reconstruir solo un servicio:

```bash
docker compose up -d --build backend-api
docker compose up -d --build frontend-thymeleaf
docker compose up -d --build frontend-vue
```

Entrar en MySQL desde Docker:

```bash
docker compose exec mysql-db mysql -uroot -proot123 booksocial
```

## Documentacion y videos de entrega

La documentacion del proyecto esta en `docs` y en esta misma carpeta tambien tiene enlaces para descargar el proyecto en dropbox y desde GitHub.
