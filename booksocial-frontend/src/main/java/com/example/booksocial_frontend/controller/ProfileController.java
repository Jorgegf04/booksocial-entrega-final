package com.example.booksocial_frontend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.SubscriptionResponseDTO;
import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;
import com.example.booksocial_frontend.dto.TrackingWorkRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.FileUploadClientService;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.SubscriptionClientService;
import com.example.booksocial_frontend.service.TrackingOrderClientService;
import com.example.booksocial_frontend.service.TrackingWorkClientService;
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador del perfil de usuario. Muestra perfiles, relaciones sociales, biblioteca, wishlist, suscripcion y progreso de lectura.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class ProfileController {

  private final UserClientService userService;
  private final TrackingWorkClientService trackingService;
  private final OrderClientService orderService;
  private final TrackingOrderClientService trackingOrderService;
  private final FileUploadClientService fileUploadClientService;
  private final SubscriptionClientService subscriptionService;
  /**
   * Muestra perfil con obras, seguidores, biblioteca, wishlist y comentarios.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping("/{id}")
  public String showProfile(@PathVariable Long id, HttpSession session, Model model) {

    UserResponseDTO user;
    try {
      user = userService.getUserById(id);
    } catch (Exception e) {
      return "redirect:/community";
    }

    List<TrackingWorkResponseDTO> tracking;
    try {
      tracking = userService.getTrackingByUser(id);
    } catch (Exception e) {
      tracking = List.of();
    }

    Long sessionUserId = (Long) session.getAttribute("userId");
    boolean isOwnProfile = sessionUserId != null && sessionUserId.equals(id);

    // Seguidores / siguiendo
    List<UserResponseDTO> followers;
    List<UserResponseDTO> following;
    try {
      followers = userService.getFollowers(id);
    } catch (Exception e) {
      followers = List.of();
    }
    try {
      following = userService.getFollowing(id);
    } catch (Exception e) {
      following = List.of();
    }
    boolean isFollowing = sessionUserId != null && followers.stream()
        .anyMatch(f -> f.getId().equals(sessionUserId));

    // Pedidos: solo para el propio perfil
    List<OrderResponseDTO> orders = null;
    Map<Long, TrackingOrderResponseDTO> orderTrackings = new HashMap<>();
    if (isOwnProfile) {
      try {
        orders = orderService.getOrdersByUser(id);
      } catch (Exception e) {
        log.warn("No se pudieron cargar los pedidos del usuario {}: {}", id, e.getMessage());
        orders = List.of();
      }
      for (OrderResponseDTO order : orders) {
        TrackingOrderResponseDTO t = trackingOrderService.getLatestByOrder(order.getId());
        if (t != null) {
          orderTrackings.put(order.getId(), t);
        }
      }
    }

    // SuscripciÃ³n: solo para el propio perfil con rol SUBSCRIBED
    SubscriptionResponseDTO subscription = null;
    if (isOwnProfile && "SUBSCRIBED".equals(user.getRole())) {
      try {
        subscription = subscriptionService.getByUserId(id);
      } catch (Exception e) {
        log.warn("[PROFILE] No se pudo cargar suscripciÃ³n del usuario {}: {}", id, e.getMessage());
      }
    }

    model.addAttribute("user", user);
    model.addAttribute("tracking", tracking);
    model.addAttribute("isOwnProfile", isOwnProfile);
    model.addAttribute("sessionUserId", sessionUserId);
    model.addAttribute("followers", followers);
    model.addAttribute("following", following);
    model.addAttribute("followersCount", followers.size());
    model.addAttribute("followingCount", following.size());
    model.addAttribute("isFollowing", isFollowing);
    model.addAttribute("orders", orders);
    model.addAttribute("orderTrackings", orderTrackings);
    model.addAttribute("subscription", subscription);
    model.addAttribute("updateForm", new UpdateUserRequestDTO(
        user.getUsername(), user.getEmail(),
        user.getName(), user.getSecondName(), user.getImg(),
        user.getRole(), user.getActive()));

    return "user/profile";
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
    if (sessionUserId == null) return "redirect:/auth/login";
    try {
      userService.followUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[PROFILE] Error al seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
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
    if (sessionUserId == null) return "redirect:/auth/login";
    try {
      userService.unfollowUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[PROFILE] Error al dejar de seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
  }
  /**
   * Redirige al perfil propio del usuario conectado.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping("/me")
  public String myProfile(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";
    return "redirect:/user/" + userId;
  }
  /**
   * Avanza el estado de lectura de una obra.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{userId}/tracking/{trackingId}/next-status")
  public String advanceTrackingStatus(@PathVariable Long userId,
                                       @PathVariable Long trackingId,
                                       HttpSession session,
                                       org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(userId)) {
      return "redirect:/auth/login";
    }

    try {
      List<TrackingWorkResponseDTO> trackings = userService.getTrackingByUser(userId);
      TrackingWorkResponseDTO current = trackings.stream()
          .filter(t -> t.getId().equals(trackingId))
          .findFirst()
          .orElse(null);

      if (current != null) {
        String nextStatus = getNextStatus(current.getStatus());
        trackingService.update(trackingId,
            new TrackingWorkRequestDTO(userId, current.getWorkId(), nextStatus));
      }
    } catch (Exception e) {
      log.warn("[PROFILE] Error al avanzar estado de tracking id={}: {}", trackingId, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }

    return "redirect:/user/" + userId;
  }

  @PostMapping("/{userId}/tracking/{trackingId}/delete")
  public String deleteTracking(@PathVariable Long userId,
                               @PathVariable Long trackingId,
                               HttpSession session,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(userId)) {
      return "redirect:/auth/login";
    }

    try {
      boolean belongsToUser = userService.getTrackingByUser(userId).stream()
          .anyMatch(t -> t.getId().equals(trackingId));

      if (!belongsToUser) {
        ra.addFlashAttribute("errorMsg", "No se pudo quitar esa obra de tu biblioteca.");
        return "redirect:/user/" + userId;
      }

      trackingService.delete(trackingId);
      ra.addFlashAttribute("successMsg", "Obra quitada de tu biblioteca.");
    } catch (Exception e) {
      log.warn("[PROFILE] Error al eliminar tracking id={}: {}", trackingId, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }

    return "redirect:/user/" + userId;
  }

  /**
   * Calcula el siguiente estado de lectura.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  private String getNextStatus(String current) {
    if (current == null) return "READING";
    return switch (current) {
      case "PENDING" -> "READING";
      case "READING" -> "COMPLETED";
      default -> "PENDING";
    };
  }
  /**
   * Cancela la suscripcion premium del usuario.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{id}/subscription/cancel")
  public String cancelSubscription(@PathVariable Long id,
                                   HttpSession session,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(id)) {
      return "redirect:/auth/login";
    }
    try {
      subscriptionService.cancel(id);
      session.setAttribute("role", "REGISTERED");
      ra.addFlashAttribute("successMsg", "SuscripciÃ³n cancelada correctamente.");
    } catch (Exception e) {
      log.warn("[PROFILE] Error al cancelar suscripciÃ³n del usuario {}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
  }
  /**
   * Actualiza los datos basicos del perfil.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/{id}/update")
  public String updateProfile(@PathVariable Long id,
                               @ModelAttribute UpdateUserRequestDTO form,
                               @RequestParam(required = false) MultipartFile avatarFile,
                               HttpSession session,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(id)) {
      return "redirect:/auth/login";
    }

    try {
      if (avatarFile != null && !avatarFile.isEmpty()) {
        form.setImg(fileUploadClientService.uploadImage(avatarFile));
      }
      userService.updateUser(id, form);
      ra.addFlashAttribute("successMsg", "Perfil actualizado correctamente.");
    } catch (Exception e) {
      log.warn("[PROFILE] Error al actualizar usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
  }
}
