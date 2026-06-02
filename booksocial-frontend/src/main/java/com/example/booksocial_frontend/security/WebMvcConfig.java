package com.example.booksocial_frontend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Configuracion MVC que registra interceptores y reglas propias del frontend.
 * Se usa para organizar mejor la logica del frontend Thymeleaf.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final AdminInterceptor adminInterceptor;
  private final BackendStatusInterceptor backendStatusInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(backendStatusInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/favicon.ico",
            "/actuator/**");

    registry.addInterceptor(adminInterceptor)
        .addPathPatterns("/admin/**");
  }
}
