package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.booksocial_backend.domain.commerce.TrackingOrder;
import com.example.booksocial_backend.domain.commerce.TrackingOrderStatus;
import com.example.booksocial_backend.DTO.commerce.TrackingOrderRequestDTO;
import com.example.booksocial_backend.DTO.commerce.TrackingOrderResponseDTO;
import com.example.booksocial_backend.domain.commerce.Order;
import com.example.booksocial_backend.exception.OrderNotFoundException;
import com.example.booksocial_backend.repository.OrderRepository;
import com.example.booksocial_backend.repository.TrackingOrderRepository;
import com.example.booksocial_backend.service.TrackingOrderService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para seguimiento de pedidos.
 *
 * Define operaciones para registrar y consultar estados de tracking de pedidos. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrackingOrderServiceImpl implements TrackingOrderService {

  private static final Logger log = LoggerFactory.getLogger(TrackingOrderServiceImpl.class);

  /** Repositorio JPA utilizado para consultar y registrar historicos de tracking. */
  private final TrackingOrderRepository trackingRepository;
  /** Repositorio JPA utilizado para validar que el pedido asociado existe. */
  private final OrderRepository orderRepository;

  /**
   * Registra un nuevo estado de seguimiento para un pedido.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con el estado registrado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public TrackingOrderResponseDTO addTracking(TrackingOrderRequestDTO request) {

    log.info("[TRACKING_ORDER] [ADD] [START] orderId={} status={}", request.getOrderId(), request.getTrackingStatus());
    TrackingOrder tracking = new TrackingOrder();

    tracking.setStatus(request.getTrackingStatus());
    tracking.setDate(LocalDateTime.now());
    Order order = orderRepository.findById(request.getOrderId())
        .orElseThrow(() -> new OrderNotFoundException(request.getOrderId()));

    tracking.setOrder(order);

    validateTracking(tracking);

    validateStateTransition(request.getOrderId(), request.getTrackingStatus());

    TrackingOrder saved = trackingRepository.save(tracking);

    log.info("[TRACKING_ORDER] [ADD] [SUCCESS] id={} orderId={} status={}", saved.getId(), request.getOrderId(), request.getTrackingStatus());
    return mapToDTO(saved);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param tracking valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private TrackingOrderResponseDTO mapToDTO(TrackingOrder tracking) {
    Order order = tracking.getOrder();
    return new TrackingOrderResponseDTO(
        tracking.getId(),
        tracking.getStatus(),
        tracking.getStatus() != null ? mapStatusLabel(tracking.getStatus()) : null,
        tracking.getDate(),
        order != null ? order.getId() : null,

        (order != null && order.getUser() != null) ? order.getUser().getId() : null,
        (order != null && order.getUser() != null) ? order.getUser().getUsername() : null,

        tracking.getStatus() == TrackingOrderStatus.DELIVERED);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param status estado asociado a la operacion
   * @return texto generado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private String mapStatusLabel(TrackingOrderStatus status) {
    return switch (status) {
      case PREPARING -> "Preparando pedido";
      case SHIPPED -> "Enviado";
      case IN_TRANSIT -> "En tránsito";
      case DELIVERED -> "Entregado";
      case CANCELED -> "Cancelado";
    };
  }

  /**
   * Valida los datos básicos de tracking.
   */
  private void validateTracking(TrackingOrder tracking) {

    if (tracking == null) {
      throw new IllegalArgumentException("El tracking no puede ser nulo");
    }

    if (tracking.getStatus() == null) {
      throw new IllegalArgumentException("El estado es obligatorio");
    }

    if (tracking.getOrder() == null || tracking.getOrder().getId() == null) {
      throw new IllegalArgumentException("Debe existir un pedido válido");
    }
  }

  /**
   * Valida la transición de estados de un pedido.
   */
  private void validateStateTransition(Long orderId, TrackingOrderStatus newStatus) {

    // Se compara con el ultimo estado registrado para impedir cambios desde estados finales.
    List<TrackingOrder> history = trackingRepository.findByOrderIdOrderByDateAsc(orderId);

    if (history.isEmpty()) {
      return;
    }

    TrackingOrderStatus lastStatus = history.get(history.size() - 1).getStatus();

    if (lastStatus == TrackingOrderStatus.DELIVERED) {
      throw new IllegalArgumentException("El pedido ya ha sido entregado");
    }

    if (lastStatus == TrackingOrderStatus.CANCELED) {
      throw new IllegalArgumentException("El pedido está cancelado");
    }
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param orderId identificador del pedido asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public TrackingOrderResponseDTO getLatestTracking(Long orderId) {

    return trackingRepository
        .findFirstByOrder_IdOrderByDateDesc(orderId)
        .map(this::mapToDTO)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No hay tracking para el pedido: " + orderId));
  }
}
