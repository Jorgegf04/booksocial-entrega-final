package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.EventClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de eventos. Lista eventos disponibles y permite apuntarse o abandonar eventos segun el estado de la sesion.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

  private final EventClientService eventService;

  /**
   * Muestra eventos disponibles y marca los del usuario.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping
  public String listEvents(Model model, HttpSession session) {
    try {
      List<EventResponseDTO> events = eventService.getUpcomingEvents();

      // Separar featured (primero) del resto
      EventResponseDTO featured = events.isEmpty() ? null : events.get(0);
      EventResponseDTO secondary = events.size() > 1 ? events.get(1) : null;
      List<EventResponseDTO> upcoming = events.size() > 2 ? events.subList(2, events.size()) : List.of();

      model.addAttribute("featured", featured);
      model.addAttribute("secondary", secondary);
      model.addAttribute("upcoming", upcoming);
    } catch (Exception e) {
      log.warn("[EVENTS] Error al cargar eventos: {}", e.getMessage());
      model.addAttribute("featured", null);
      model.addAttribute("secondary", null);
      model.addAttribute("upcoming", List.of());
    }

    Long userId = (Long) session.getAttribute("userId");
    model.addAttribute("sessionUserId", userId);
    model.addAttribute("sessionRole", session.getAttribute("role"));

    return "events/index";
  }

  /**
   * Gestiona la ruta asociada al metodo join.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{id}/join")
  public String join(@PathVariable Long id, HttpSession session,
                     RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    if (!canJoinEvents(session)) {
      ra.addFlashAttribute("errorMsg",
          "Solo los suscriptores pueden apuntarse a eventos. ¡Únete a La Caja del Curador!");
      return "redirect:/events";
    }

    try {
      eventService.joinEvent(id, userId);
      ra.addFlashAttribute("successMsg", "Te has unido al evento correctamente.");
    } catch (Exception e) {
      log.warn("[EVENTS] Error al unirse al evento id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/events";
  }

  /**
   * Gestiona la ruta asociada al metodo leave.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{id}/leave")
  public String leave(@PathVariable Long id, HttpSession session,
                      RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    if (!canJoinEvents(session)) {
      ra.addFlashAttribute("errorMsg",
          "Solo los suscriptores pueden apuntarse a eventos. ¡Únete a La Caja del Curador!");
      return "redirect:/events";
    }

    try {
      eventService.leaveEvent(id, userId);
      ra.addFlashAttribute("successMsg", "Has abandonado el evento.");
    } catch (Exception e) {
      log.warn("[EVENTS] Error al abandonar el evento id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/events";
  }

  /**
   * Comprueba si la sesion permite participar en eventos.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  private boolean canJoinEvents(HttpSession session) {
    Object role = session.getAttribute("role");
    return "SUBSCRIBED".equals(role) || "ADMIN".equals(role);
  }
}
