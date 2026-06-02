package com.example.booksocial_frontend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.booksocial_frontend.dto.CartItemDTO;

import jakarta.servlet.http.HttpSession;

/**
 * Servicio de cart del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: no usa ruta REST, trabaja con la sesion HTTP.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class CartService {

  private static final String CART_KEY = "cart";

  /**
   * Obtiene el carrito guardado en sesion o crea uno vacio si no existe.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @SuppressWarnings("unchecked")
  public List<CartItemDTO> getCart(HttpSession session) {
    List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute(CART_KEY);
    if (cart == null) {
      cart = new ArrayList<>();
      session.setAttribute(CART_KEY, cart);
    }
    return cart;
  }

  /**
   * Anade un producto al carrito o aumenta su cantidad si ya estaba.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void addItem(HttpSession session, CartItemDTO item) {
    List<CartItemDTO> cart = getCart(session);
    for (CartItemDTO existing : cart) {
      if (existing.getProductId().equals(item.getProductId())) {
        existing.setQuantity(existing.getQuantity() + item.getQuantity());
        return;
      }
    }
    cart.add(item);
  }

  /**
   * Elimina un producto del carrito usando su identificador.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void removeItem(HttpSession session, Long productId) {
    List<CartItemDTO> cart = getCart(session);
    cart.removeIf(i -> i.getProductId().equals(productId));
  }

  /**
   * Actualiza la cantidad de un producto dentro del carrito.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void updateQuantity(HttpSession session, Long productId, int quantity) {
    List<CartItemDTO> cart = getCart(session);
    if (quantity <= 0) {
      removeItem(session, productId);
      return;
    }
    for (CartItemDTO item : cart) {
      if (item.getProductId().equals(productId)) {
        item.setQuantity(quantity);
        return;
      }
    }
  }

  /**
   * Vacia el carrito guardado en la sesion.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void clearCart(HttpSession session) {
    session.removeAttribute(CART_KEY);
  }

  /**
   * Calcula el numero total de productos del carrito.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public int getTotalItems(HttpSession session) {
    return getCart(session).stream().mapToInt(CartItemDTO::getQuantity).sum();
  }

  /**
   * Calcula el importe total del carrito.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public double getTotal(HttpSession session) {
    return getCart(session).stream().mapToDouble(CartItemDTO::getSubtotal).sum();
  }
}
