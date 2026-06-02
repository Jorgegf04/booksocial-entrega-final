package com.example.booksocial_frontend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.example.booksocial_frontend.service.BackendStatusService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Anade el aviso de backend caido a cualquier vista renderizada por Thymeleaf.
 */
@Component
@RequiredArgsConstructor
public class BackendStatusInterceptor implements HandlerInterceptor {

  public static final String BACKEND_DOWN_MESSAGE =
      "El servidor no responde ahora mismo. Puede estar iniciandose o tardando mas de lo normal.";

  private final BackendStatusService backendStatusService;

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView) {

    if (modelAndView == null || isRedirect(modelAndView)) {
      return;
    }

    if (backendStatusService.isBackendDown() && !modelAndView.getModel().containsKey("apiError")) {
      modelAndView.addObject("apiError", BACKEND_DOWN_MESSAGE);
    }
  }

  private boolean isRedirect(ModelAndView modelAndView) {
    String viewName = modelAndView.getViewName();
    return viewName != null && viewName.startsWith("redirect:");
  }
}
