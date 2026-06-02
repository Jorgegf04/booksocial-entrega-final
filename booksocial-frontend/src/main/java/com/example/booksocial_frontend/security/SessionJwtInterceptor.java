package com.example.booksocial_frontend.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

// Codigo de la ilustración 44
/**
 * Interceptor que anade el token JWT de la sesion a las peticiones que van al
 * backend.
 * Se usa para organizar mejor la logica del frontend Thymeleaf.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Component
public class SessionJwtInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpSession session = attrs.getRequest().getSession(false);
            if (session != null) {
                String jwt = (String) session.getAttribute("JWT");
                if (jwt != null && !jwt.isBlank()) {
                    request.getHeaders().setBearerAuth(jwt);
                }
            }
        }
        return execution.execute(request, body);
    }
}
