package com.example.booksocial_frontend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Codigo de ilustracion 47
/**
 * Interceptor que revisa si el usuario tiene rol ADMIN antes de entrar al panel
 * de administracion.
 * Se usa para organizar mejor la logica del frontend Thymeleaf.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response,
      Object handler) throws Exception {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("role") == null
        || !"ADMIN".equals(session.getAttribute("role"))) {
      response.sendRedirect("/auth/login");
      return false;
    }

    return true;
  }
}
