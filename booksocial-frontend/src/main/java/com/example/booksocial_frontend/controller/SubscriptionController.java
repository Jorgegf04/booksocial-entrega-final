package com.example.booksocial_frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.SubscriptionClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de suscripciones premium. Muestra la pantalla premium y permite
 * activar, cancelar o reactivar la suscripcion.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o
 * respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionClientService subscriptionService;

    /**
     * Muestra la pagina premium y el estado de suscripcion.
     * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o
     * respuestas del frontend.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    @GetMapping("/premium")
    public String premium(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId != null && "SUBSCRIBED".equals(role)) {
            return "redirect:/user/" + userId;
        }

        model.addAttribute("canSubscribe", userId != null && "REGISTERED".equals(role));
        return "subscription/premium";
    }

    /**
     * Activa la suscripcion premium del usuario conectado.
     * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o
     * respuestas del frontend.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    @PostMapping("/activate")
    public String activate(HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/auth/login";

        try {
            subscriptionService.activate(userId);
            session.setAttribute("role", "SUBSCRIBED");
            ra.addFlashAttribute("successMsg", "¡Bienvenido a La Caja del Curador! Tu suscripción está activa.");
        } catch (Exception e) {
            log.warn("[SUBSCRIPTION] Error al activar suscripción del usuario {}: {}", userId, e.getMessage());
            ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
            return "redirect:/subscription/premium";
        }
        return "redirect:/user/" + userId;
    }

    /**
     * Cancela la suscripcion premium del usuario conectado.
     * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o
     * respuestas del frontend.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    @PostMapping("/cancel")
    public String cancel(HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/auth/login";

        try {
            subscriptionService.cancel(userId);
            session.setAttribute("role", "REGISTERED");
            ra.addFlashAttribute("successMsg", "Suscripción cancelada correctamente.");
        } catch (Exception e) {
            log.warn("[SUBSCRIPTION] Error al cancelar suscripción del usuario {}: {}", userId, e.getMessage());
            ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
        }
        return "redirect:/user/" + userId;
    }

    /**
     * Reactiva la suscripcion premium del usuario conectado.
     * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o
     * respuestas del frontend.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    @PostMapping("/reactivate")
    public String reactivate(HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:/auth/login";

        try {
            subscriptionService.activate(userId);
            session.setAttribute("role", "SUBSCRIBED");
            ra.addFlashAttribute("successMsg", "Suscripción reactivada correctamente.");
        } catch (Exception e) {
            log.warn("[SUBSCRIPTION] Error al reactivar suscripción del usuario {}: {}", userId, e.getMessage());
            ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
        }
        return "redirect:/user/" + userId;
    }
}
