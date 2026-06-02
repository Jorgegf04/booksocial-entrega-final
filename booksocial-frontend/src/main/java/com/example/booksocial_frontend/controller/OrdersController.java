package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.service.OrderClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de pedidos del usuario. Consulta los pedidos de la sesion actual y los muestra en la vista privada.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

  private final OrderClientService orderService;

  /**
   * Muestra pedidos del usuario conectado o redirige al login.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping
  public String myOrders(HttpSession session, Model model) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      List<OrderResponseDTO> orders = orderService.getOrdersByUser(userId);
      model.addAttribute("orders", orders);
    } catch (Exception e) {
      log.warn("[ORDERS] Error al cargar pedidos de userId={}: {}", userId, e.getMessage());
      model.addAttribute("orders", List.of());
    }

    return "orders/history";
  }
}
