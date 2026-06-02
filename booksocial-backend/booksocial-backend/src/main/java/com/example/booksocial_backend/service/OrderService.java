package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.commerce.OrderRequestDTO;
import com.example.booksocial_backend.DTO.commerce.OrderResponseDTO;

/**
 * Contrato de servicio para la gestion de pedidos.
 * Define las operaciones de creacion, consulta y eliminacion de pedidos.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface OrderService {

  /**
   * Crea un nuevo pedido dentro de la aplicación.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del pedido creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  OrderResponseDTO createOrder(OrderRequestDTO request);

  /**
   * Obtiene un pedido por su ID.
   *
   * @param id ID del pedido sobre el que se realiza la operacion
   * @return DTO de respuesta del pedido solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  OrderResponseDTO getOrderById(Long id);

  /**
   * Obtiene todos los pedidos disponibles dentro de la aplicación.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<OrderResponseDTO> getAllOrders();

  /**
   * Obtiene los pedidos de un usuariuo en concreto
   *
   * @param userId ID del usuario asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<OrderResponseDTO> getOrdersByUser(Long userId);

  /**
   * Elimina un pedido dentro de la aplicación.
   *
   * @param id ID del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteOrder(Long id);
}