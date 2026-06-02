package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.commerce.TrackingOrderRequestDTO;
import com.example.booksocial_backend.DTO.commerce.TrackingOrderResponseDTO;

/**
 * Contrato de servicio para seguimiento de pedidos.
 * Define operaciones para registrar y consultar estados de tracking de pedidos.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface TrackingOrderService {

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
  TrackingOrderResponseDTO addTracking(TrackingOrderRequestDTO request);

  /**
   * Obtiene el ultimo tracking de una seguimiento de pedido
   *
   * @param orderId identificador del pedido asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  TrackingOrderResponseDTO getLatestTracking(Long orderId);
}
