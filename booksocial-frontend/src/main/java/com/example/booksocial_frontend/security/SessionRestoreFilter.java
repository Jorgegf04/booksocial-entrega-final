package com.example.booksocial_frontend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro que recupera la sesion del usuario desde cookies cuando la aplicacion vuelve a cargar.
 * Se usa para organizar mejor la logica del frontend Thymeleaf.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Component
@Order(1)
public class SessionRestoreFilter implements Filter {

  private static final String COOKIE_JWT  = "bs_jwt";
  private static final String COOKIE_UID  = "bs_uid";
  private static final String COOKIE_USR  = "bs_usr";
  private static final String COOKIE_ROLE = "bs_role";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("userId") == null) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        String jwt = null, uid = null, usr = null, role = null;

        for (Cookie c : cookies) {
          switch (c.getName()) {
            case COOKIE_JWT  -> jwt  = c.getValue();
            case COOKIE_UID  -> uid  = c.getValue();
            case COOKIE_USR  -> usr  = c.getValue();
            case COOKIE_ROLE -> role = c.getValue();
          }
        }

        if (jwt != null && uid != null && !jwt.isBlank() && !uid.isBlank()) {
          HttpSession s = request.getSession(true);
          s.setAttribute("JWT",      jwt);
          s.setAttribute("userId",   Long.parseLong(uid));
          s.setAttribute("username", usr);
          s.setAttribute("role",     role);
        }
      }
    }

    chain.doFilter(req, res);
  }
}
