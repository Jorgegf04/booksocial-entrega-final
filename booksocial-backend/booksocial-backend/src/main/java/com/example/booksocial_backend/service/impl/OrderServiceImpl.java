package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.domain.commerce.Order;
import com.example.booksocial_backend.domain.commerce.OrderLine;
import com.example.booksocial_backend.DTO.commerce.OrderLineResponseDTO;
import com.example.booksocial_backend.DTO.commerce.OrderRequestDTO;
import com.example.booksocial_backend.DTO.commerce.OrderResponseDTO;
import com.example.booksocial_backend.domain.catalog.Product;
import com.example.booksocial_backend.domain.user.Role;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.exception.InsufficientStockException;
import com.example.booksocial_backend.exception.OrderNotFoundException;
import com.example.booksocial_backend.exception.ProductNotFoundException;

import com.example.booksocial_backend.repository.OrderRepository;
import com.example.booksocial_backend.repository.ProductRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.service.OrderService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de pedidos.
 *
 * Define las operaciones de creacion, consulta y eliminacion de pedidos. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

  private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar pedidos. */
  private final OrderRepository orderRepository;
  /** Repositorio JPA utilizado para consultar productos y actualizar stock. */
  private final ProductRepository productRepository;
  /** Repositorio JPA utilizado para resolver el usuario asociado al pedido. */
  private final UserRepository userRepository;

  /**
   * Crea un nuevo recurso aplicando las validaciones de negocio correspondientes.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del recurso creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public OrderResponseDTO createOrder(OrderRequestDTO request) {

    log.info("[ORDER] [CREATE] [START] userId={} guestEmail={}", request.getUserId(), request.getGuestEmail());

    if (request.getOrderLines() == null || request.getOrderLines().isEmpty()) {
      throw new IllegalArgumentException("El pedido debe contener al menos una línea de pedido");
    }

    if (request.getUserId() == null && (request.getGuestEmail() == null || request.getGuestEmail().isBlank())) {
      throw new IllegalArgumentException("Se requiere userId o guestEmail para crear un pedido");
    }

    User user = request.getUserId() != null ? userRepository.findById(request.getUserId()).orElse(null) : null;

    // Los pedidos de usuarios suscritos o administradores aplican el descuento comercial vigente.
    boolean hasSubscriberDiscount = user != null
        && (user.getRole() == Role.SUBSCRIBED || user.getRole() == Role.ADMIN);
    double discountFactor = hasSubscriberDiscount ? 0.70 : 1.0;
    if (hasSubscriberDiscount) {
      log.info("[ORDER] [CREATE] Descuento suscriptor/admin (30%) aplicado para userId={}", user.getId());
    }

    Order order = Order.builder()
        .user(user)
        .guestEmail(user == null ? request.getGuestEmail() : null)
        .date(LocalDateTime.now())
        .build();

    List<OrderLine> lines = request.getOrderLines().stream()
        .map(dto -> {
          Product product = productRepository.findById(dto.getProductId())
              .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con id: " + dto.getProductId()));

          if (product.getStock() == null || product.getStock() < dto.getQuantity()) {
            log.warn("[ORDER] [CREATE] [WARN] Stock insuficiente para productId={} (disponible={}, solicitado={})",
                product.getId(), product.getStock(), dto.getQuantity());
            throw new InsufficientStockException(product.getId(),
                dto.getQuantity(), product.getStock() != null ? product.getStock() : 0);
          }

          // El stock se descuenta dentro de la misma transaccion que crea el pedido.
          product.setStock(product.getStock() - dto.getQuantity());
          productRepository.save(product);

          double unitaryPrice = product.getPrice() * discountFactor;

          return OrderLine.builder()
              .product(product)
              .quantity(dto.getQuantity())
              .unitaryPrice(unitaryPrice)
              .order(order)
              .build();
        })
        .toList();

    order.setOrderLines(new ArrayList<>(lines));

    double total = calculateTotal(lines);
    order.setTotal(total);

    Order saved = orderRepository.save(order);
    log.info("[ORDER] [CREATE] [SUCCESS] id={} total={}", saved.getId(), saved.getTotal());
    return mapToDTO(saved);
  }

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public OrderResponseDTO getOrderById(Long id) {
    log.info("[ORDER] [READ] id={}", id);
    return mapToDTO(getOrderEntityById(id));
  }

  /**
   * Obtiene todos los recursos disponibles para este modulo.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getAllOrders() {
    return orderRepository.findAll()
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Obtiene recursos asociados a un usuario.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return lista de DTOs asociados al usuario indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> getOrdersByUser(Long userId) {
    return orderRepository.findByUserId(userId)
        .stream()
        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Elimina, cancela o desactiva el recurso indicado segun la regla de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void deleteOrder(Long id) {
    log.info("[ORDER] [DELETE] [START] id={}", id);
    Order order = getOrderEntityById(id);
    orderRepository.delete(order);
    log.info("[ORDER] [DELETE] [SUCCESS] id={}", id);
  }

  /**
   * Ejecuta la operacion de servicio calculateTotal.
   *
   * @param lines valor utilizado por la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private double calculateTotal(List<OrderLine> lines) {
    return lines.stream()
        .mapToDouble(l -> l.getUnitaryPrice() * l.getQuantity())
        .sum();
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param order valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private OrderResponseDTO mapToDTO(Order order) {

    List<OrderLine> safeLines = order.getOrderLines() != null ? order.getOrderLines() : List.of();

    List<OrderLineResponseDTO> lines = safeLines.stream()
        .map(l -> new OrderLineResponseDTO(
            l.getProduct().getId(),
            l.getProduct().getEdition() != null ? l.getProduct().getEdition().getTitle() : null,
            l.getProduct().getPrice(),
            l.getQuantity(),
            l.getQuantity() * l.getProduct().getPrice()))
        .toList();

    int totalItems = safeLines.stream()
        .mapToInt(OrderLine::getQuantity)
        .sum();

    Long userId = order.getUser() != null ? order.getUser().getId() : null;
    String username = order.getUser() != null ? order.getUser().getUsername() : null;

    return new OrderResponseDTO(
        order.getId(),
        order.getDate(),
        order.getTotal(),
        userId,
        username,
        order.getGuestEmail(),
        totalItems,
        lines);
  }

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private Order getOrderEntityById(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
  }
}
