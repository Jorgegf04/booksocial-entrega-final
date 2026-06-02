# Dockerfile del backend
# Crea la imagen de la API Spring Boot para poder ejecutarla en un contenedor.
# Compila el proyecto con Maven, copaia el .jar final y arranca la aplicación en el puerto 999

FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -q

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

RUN apk add --no-cache curl

RUN mkdir -p /app/images && chown appuser:appgroup /app/images

COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "app.jar"]
