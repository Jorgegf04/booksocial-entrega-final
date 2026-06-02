package com.example.booksocial_backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.commerce.OrderLineRequestDTO;
import com.example.booksocial_backend.DTO.commerce.OrderLineResponseDTO;
import com.example.booksocial_backend.domain.catalog.Product;
import com.example.booksocial_backend.domain.commerce.Order;
import com.example.booksocial_backend.domain.commerce.OrderLine;
import com.example.booksocial_backend.repository.OrderLineRepository;
import com.example.booksocial_backend.repository.OrderRepository;
import com.example.booksocial_backend.repository.ProductRepository;
import com.example.booksocial_backend.service.OrderLineService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para lineas de pedido.
 *
 * Define la creacion de lineas individuales o masivas dentro del flujo de compra. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderLineServiceImpl implements OrderLineService {

  /** Repositorio JPA utilizado para persistir y consultar lineas de pedido. */
  private final OrderLineRepository orderLineRepository;
  /** Repositorio JPA utilizado para validar productos en las lineas de pedido. */
  private final ProductRepository productRepository;
  /** Repositorio JPA utilizado para validar el pedido asociado a cada linea. */
  private final OrderRepository orderRepository;

  // =========================
  // CREATE
  // =========================

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
  public OrderLineResponseDTO createOrderLine(OrderLineRequestDTO request) {

    // 1. Validación básica
    if (request == null) {
      throw new IllegalArgumentException("La petición no puede ser nula");
    }

    if (request.getProductId() == null) {
      throw new IllegalArgumentException("El productId es obligatorio");
    }

    if (request.getOrderId() == null) {
      throw new IllegalArgumentException("El orderId es obligatorio");
    }

    if (request.getQuantity() == null || request.getQuantity() <= 0) {
      throw new IllegalArgumentException("Cantidad inválida");
    }

    // 2. Buscar producto
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.getProductId()));

    // Valida stock antes de crear la linea para evitar ventas por encima de la disponibilidad.
    if (product.getStock() < request.getQuantity()) {
      throw new IllegalArgumentException("Stock insuficiente");
    }

    // 3. Buscar pedido
    Order order = orderRepository.findById(request.getOrderId())
        .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + request.getOrderId()));

    // 4. Construir entidad
    OrderLine line = new OrderLine();
    line.setProduct(product);
    line.setOrder(order);
    line.setQuantity(request.getQuantity());

    // 🔐 Seguridad: precio SIEMPRE backend
    line.setUnitaryPrice(product.getPrice());

    // 5. Actualizar stock
    product.setStock(product.getStock() - request.getQuantity());

    // 6. Guardar
    OrderLine saved = orderLineRepository.save(line);

    // 7. Mapear a DTO
    return mapToDTO(saved);
  }
  // =========================
  // MAPPER
  // =========================

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param line valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private OrderLineResponseDTO mapToDTO(OrderLine line) {
    Product product = line.getProduct();
    String editionTitle = (product != null && product.getEdition() != null)
        ? product.getEdition().getTitle()
        : null;

    return new OrderLineResponseDTO(
        product != null ? product.getId() : null,
        editionTitle,
        line.getUnitaryPrice(),
        line.getQuantity(),
        line.getUnitaryPrice() != null && line.getQuantity() != null
            ? line.getUnitaryPrice() * line.getQuantity()
            : null);
  }
}
