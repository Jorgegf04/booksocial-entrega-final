package com.example.booksocial_frontend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.booksocial_frontend.dto.CartItemDTO;
import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.CartService;
import com.example.booksocial_frontend.service.MailService;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.ProductClientService;
import com.example.booksocial_frontend.service.UserClientService;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.WorkClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador del carrito de compra. Muestra productos, permite anadir, quitar, actualizar cantidades, sincronizar y crear pedidos.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

  private final CartService cartService;
  private final ProductClientService productService;
  private final WorkClientService workService;
  private final OrderClientService orderService;
  private final UserClientService userService;
  private final MailService mailService;

  private static final double SUBSCRIBER_DISCOUNT = 0.30;
  /**
   * Muestra el carrito actual y calcula totales y descuentos.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping
  public String viewCart(HttpSession session, Model model) {
    List<CartItemDTO> items = cartService.getCart(session);
    double subtotal = cartService.getTotal(session);
    double shipping = items.isEmpty() ? 0.0 : 5.50;

    Object role = session.getAttribute("role");
    boolean isSubscribed = "SUBSCRIBED".equals(role) || "ADMIN".equals(role);
    double discountedSubtotal = isSubscribed ? subtotal * (1 - SUBSCRIBER_DISCOUNT) : subtotal;
    double total = discountedSubtotal + shipping;

    model.addAttribute("cartItems", items);
    model.addAttribute("subtotal", subtotal);
    model.addAttribute("discountedSubtotal", discountedSubtotal);
    model.addAttribute("discountAmount", subtotal - discountedSubtotal);
    model.addAttribute("isSubscribed", isSubscribed);
    model.addAttribute("shipping", shipping);
    model.addAttribute("total", total);

    // Sugerencias: obras con imagen, excluyendo las que ya estÃ¡n en el carrito
    try {
      List<Long> inCart = items.stream().map(CartItemDTO::getProductId).toList();

      // Obtener los workIds de los productos que ya estÃ¡n en el carrito
      Set<Long> workIdsInCart = productService.getAvailableProducts().stream()
          .filter(p -> inCart.contains(p.getId()))
          .map(ProductResponseDTO::getWorkId)
          .collect(Collectors.toSet());

      // Obtener todas las obras, mezclarlas y tomar 4 que no estÃ©n en el carrito
      List<WorkResponseDTO> allWorks = workService.getAllWorks();
      Collections.shuffle(allWorks);
      List<WorkResponseDTO> suggestions = allWorks.stream()
          .filter(w -> !workIdsInCart.contains(w.getId())
              && w.getImg() != null && !w.getImg().isBlank())
          .limit(4)
          .toList();
      model.addAttribute("suggestions", suggestions);
    } catch (Exception e) {
      model.addAttribute("suggestions", List.of());
    }

    return "cart/index";
  }
  /**
   * Anade un producto al carrito y valida el stock.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/add")
  public String addToCart(@RequestParam Long productId,
                          @RequestParam(defaultValue = "1") int quantity,
                          HttpSession session,
                          RedirectAttributes ra) {
    try {
      ProductResponseDTO product = productService.getProductById(productId);

      if (product == null) {
        ra.addFlashAttribute("cartError", "Producto no encontrado.");
        return "redirect:/catalog";
      }

      if (product.getStock() == null || product.getStock() < quantity) {
        ra.addFlashAttribute("cartError", "Stock insuficiente.");
        return "redirect:/work/" + (product.getWorkId() != null ? product.getWorkId() : "");
      }

      if (product.getPrice() == null) {
        ra.addFlashAttribute("cartError", "Producto sin precio asignado.");
        return "redirect:/work/" + (product.getWorkId() != null ? product.getWorkId() : "");
      }

      // Obtener img de la obra
      String img = null;
      try {
        if (product.getWorkId() != null) {
          WorkResponseDTO work = workService.getWorkById(product.getWorkId());
          if (work != null) img = work.getImg();
        }
      } catch (Exception ignored) {}

      CartItemDTO item = new CartItemDTO(
          product.getId(),
          product.getWorkTitle(),
          product.getEditionTitle() != null ? product.getEditionTitle() : "EdiciÃ³n " + product.getEditionId(),
          product.getEditorialName(),
          img,
          product.getPrice(),
          quantity
      );

      cartService.addItem(session, item);
      ra.addFlashAttribute("cartSuccess", "Producto aÃ±adido al carrito.");
    } catch (Exception e) {
      ra.addFlashAttribute("cartError", "No se pudo aÃ±adir el producto.");
    }
    return "redirect:/cart";
  }
  /**
   * Quita un producto del carrito de sesion.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/remove/{productId}")
  public String removeFromCart(@PathVariable Long productId, HttpSession session) {
    cartService.removeItem(session, productId);
    return "redirect:/cart";
  }

  /**
   * Actualiza la cantidad de un producto guardado en el carrito.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/update")
  public String updateQuantity(@RequestParam Long productId,
                               @RequestParam int quantity,
                               HttpSession session) {
    cartService.updateQuantity(session, productId, quantity);
    return "redirect:/cart";
  }

  /**
   * Sincroniza el carrito con los datos enviados por la vista.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/sync")
  @ResponseBody
  public ResponseEntity<Void> syncCart(
      @RequestBody List<Map<String, Object>> items,
      HttpSession session) {

    if (!cartService.getCart(session).isEmpty()) {
      return ResponseEntity.ok().build();
    }

    for (Map<String, Object> item : items) {
      try {
        Object pidRaw = item.get("productId");
        Object qtyRaw = item.get("quantity");
        if (pidRaw == null || qtyRaw == null) continue;

        Long productId = Long.valueOf(pidRaw.toString());
        int quantity   = Integer.parseInt(qtyRaw.toString());

        ProductResponseDTO product = productService.getProductById(productId);
        if (product == null || product.getPrice() == null) continue;

        String img = null;
        try {
          if (product.getWorkId() != null) {
            WorkResponseDTO work = workService.getWorkById(product.getWorkId());
            if (work != null) img = work.getImg();
          }
        } catch (Exception ignored) {}

        cartService.addItem(session, new CartItemDTO(
            product.getId(),
            product.getWorkTitle(),
            product.getEditionTitle() != null ? product.getEditionTitle() : "EdiciÃ³n",
            product.getEditorialName(),
            img,
            product.getPrice(),
            quantity
        ));
      } catch (Exception ignored) {
        // Producto eliminado o sin stock â†’ se omite silenciosamente
      }
    }

    return ResponseEntity.ok().build();
  }
  /**
   * Crea el pedido con las lineas del carrito y limpia la sesion.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/checkout")
  public String checkout(@RequestParam(required = false) String guestEmail,
                         HttpSession session,
                         RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");

    List<CartItemDTO> items = cartService.getCart(session);
    if (items.isEmpty()) {
      ra.addFlashAttribute("cartError", "El carrito estÃ¡ vacÃ­o.");
      return "redirect:/cart";
    }

    if (userId == null && (guestEmail == null || guestEmail.isBlank())) {
      ra.addFlashAttribute("cartError", "Introduce un email para recibir la confirmaciÃ³n del pedido.");
      return "redirect:/cart";
    }

    try {
      List<Map<String, Object>> lines = items.stream()
          .map(i -> Map.<String, Object>of(
              "productId", i.getProductId(),
              "quantity", i.getQuantity()
          ))
          .toList();

      String cleanGuestEmail = guestEmail != null ? guestEmail.trim() : null;
      OrderResponseDTO order = orderService.createOrder(userId, cleanGuestEmail, lines);
      cartService.clearCart(session);

      if (userId != null) {
        ra.addFlashAttribute("orderSuccess", true);
        try {
          UserResponseDTO user = userService.getUserById(userId);
          String username = (String) session.getAttribute("username");
          mailService.sendOrderConfirmation(user.getEmail(), username, order);
        } catch (Exception ignored) {}

        return "redirect:/orders";
      }

      try {
        mailService.sendOrderConfirmation(cleanGuestEmail, "cliente", order);
      } catch (Exception ignored) {}

      ra.addFlashAttribute("cartSuccess",
          "Pedido #" + order.getId() + " confirmado. Te hemos enviado el resumen a " + cleanGuestEmail + ".");
      return "redirect:/cart";
    } catch (Exception e) {
      log.warn("[CART] Error al procesar checkout userId={}: {}", userId, e.getMessage());
      ra.addFlashAttribute("cartError", "Error al procesar el pedido: " + ApiErrorUtils.extractApiError(e));
      return "redirect:/cart";
    }
  }
}
