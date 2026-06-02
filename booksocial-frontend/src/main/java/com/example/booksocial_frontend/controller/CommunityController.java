package com.example.booksocial_frontend.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de comunidad. Muestra usuarios registrados y permite seguir o dejar de seguir perfiles desde la sesion actual.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

  private final UserClientService userService;

  /**
   * Muestra el listado principal, aplica filtros y prepara la paginacion.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping
  public String index(HttpSession session, Model model) {
    Long sessionUserId = (Long) session.getAttribute("userId");

    List<UserResponseDTO> users;
    try {
      users = userService.getAllUsers().stream()
          .filter(u -> sessionUserId == null || !u.getId().equals(sessionUserId))
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.warn("[COMMUNITY] Error al cargar usuarios: {}", e.getMessage());
      users = List.of();
    }

    Set<Long> followingIds = Set.of();
    if (sessionUserId != null) {
      try {
        followingIds = userService.getFollowing(sessionUserId).stream()
            .map(UserResponseDTO::getId)
            .collect(Collectors.toSet());
      } catch (Exception e) {
        log.warn("[COMMUNITY] Error al cargar seguidos de userId={}: {}", sessionUserId, e.getMessage());
        followingIds = Set.of();
      }
    }

    model.addAttribute("users", users);
    model.addAttribute("followingIds", followingIds);
    model.addAttribute("sessionUserId", sessionUserId);

    return "community/index";
  }

  /**
   * Permite seguir un autor o usuario desde la cuenta iniciada.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{id}/follow")
  public String follow(@PathVariable Long id, HttpSession session,
                       org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null)
      return "redirect:/auth/login";
    try {
      userService.followUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[COMMUNITY] Error al seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/community";
  }

  /**
   * Permite dejar de seguir un autor o usuario desde la cuenta iniciada.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{id}/unfollow")
  public String unfollow(@PathVariable Long id, HttpSession session,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null)
      return "redirect:/auth/login";
    try {
      userService.unfollowUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[COMMUNITY] Error al dejar de seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/community";
  }
}
