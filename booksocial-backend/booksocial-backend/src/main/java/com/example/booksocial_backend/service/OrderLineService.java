package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.commerce.OrderLineRequestDTO;
import com.example.booksocial_backend.DTO.commerce.OrderLineResponseDTO;

/**
 * Contrato de servicio para lineas de pedido.
 * Define la creacion de lineas individuales o masivas dentro del flujo de
 * compra.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface OrderLineService {

  /**
   * Crea un nuevo una nueva linea aplicando las reglas de negocio definidas por
   * el contrato.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos de la linea creada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  OrderLineResponseDTO createOrderLine(OrderLineRequestDTO request);

}
