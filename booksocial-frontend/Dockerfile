# Este Dockerfile crea la imagen del frontend Thymeleaf de BookSocial.
# Primero compila la aplicación con Maven y después usa una imagen ligera de Java para ejecutarla.
# El contenedor arranca la aplicación en el puerto interno 8000.

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

COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
