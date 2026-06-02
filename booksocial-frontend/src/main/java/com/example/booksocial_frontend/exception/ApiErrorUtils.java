package com.example.booksocial_frontend.exception;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Utilidad para sacar un mensaje de error sencillo cuando el backend devuelve una respuesta con fallo.
 * Se usa para organizar mejor la logica del frontend Thymeleaf.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
public final class ApiErrorUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiErrorUtils() {}

    public static String extractApiError(Exception e) {
        List<String> details = extractApiErrorDetails(e);
        if (!details.isEmpty()) {
            return String.join(" ", details);
        }

        if (e instanceof HttpStatusCodeException httpEx) {
            return messageForStatus(httpEx);
        }

        if (e instanceof ResourceAccessException) {
            return "No se pudo conectar con el servidor. Comprueba que el backend este activo.";
        }

        String msg = e.getMessage();
        return isReadable(msg) ? cleanTechnicalNoise(msg) : "Ha ocurrido un error inesperado. Intentalo de nuevo.";
    }

    public static List<String> extractApiErrorDetails(Exception e) {
        if (!(e instanceof HttpStatusCodeException httpEx)) {
            return List.of();
        }

        String body = httpEx.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return List.of();
        }

        try {
            JsonNode json = OBJECT_MAPPER.readTree(body);
            Set<String> messages = new LinkedHashSet<>();

            JsonNode fieldErrors = json.get("fieldErrors");
            if (fieldErrors != null && fieldErrors.isArray()) {
                for (JsonNode fieldError : fieldErrors) {
                    String message = text(fieldError.get("message"));
                    if (isReadable(message)) {
                        messages.add(cleanTechnicalNoise(message));
                    }
                }
            }

            if (messages.isEmpty()) {
                String message = text(json.get("message"));
                if (isReadable(message)) {
                    messages.add(cleanTechnicalNoise(message));
                }
            }

            return new ArrayList<>(messages);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    public static String extractApiErrorSummary(Exception e, String fallback) {
        if (e instanceof HttpStatusCodeException httpEx) {
            List<String> details = extractApiErrorDetails(e);
            if (!details.isEmpty()) {
                return fallback;
            }
            return messageForStatus(httpEx);
        }
        return extractApiError(e);
    }

    private static String messageForStatus(HttpStatusCodeException httpEx) {
        int status = httpEx.getStatusCode().value();
        return switch (status) {
            case 400 -> "Revisa los datos introducidos. Hay algun campo incorrecto o incompleto.";
            case 401 -> "No se ha podido iniciar sesion. Revisa tus credenciales.";
            case 403 -> "No tienes permisos para realizar esta accion.";
            case 404 -> "No se encontro el recurso solicitado.";
            case 409 -> "Ya existe un registro con esos datos.";
            case 422 -> "Los datos enviados no son validos.";
            default -> status >= 500
                ? "El servidor ha tenido un problema. Intentalo de nuevo mas tarde."
                : "No se pudo completar la accion. Revisa los datos e intentalo de nuevo.";
        };
    }

    private static String text(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }

    private static boolean isReadable(String message) {
        return message != null && !message.isBlank() && !message.contains("{\"timestamp\"");
    }

    private static String cleanTechnicalNoise(String message) {
        String cleaned = message.replace("contrasena", "contraseña")
            .replace("electronico", "electrónico")
            .replace("valido", "válido")
            .replace("accion", "acción")
            .replace("sesion", "sesión")
            .replace("Intentalo", "Inténtalo");

        int jsonStart = cleaned.indexOf(": {");
        if (jsonStart > 0) {
            cleaned = cleaned.substring(0, jsonStart);
        }
        return cleaned.trim();
    }
}
