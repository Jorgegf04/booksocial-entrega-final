package com.example.booksocial_backend.service;

import com.example.booksocial_backend.DTO.user.SubscriptionRequestDTO;
import com.example.booksocial_backend.DTO.user.SubscriptionResponseDTO;

/**
 * Contrato de servicio para la gestion de suscripciones.
 * Define operaciones para activar, consultar y cancelar suscripciones de
 * usuario.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface SubscriptionService {

  /**
   * Ejecuta la activación de la suscripción
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  SubscriptionResponseDTO activateSubscription(SubscriptionRequestDTO request);

  /**
   * Elimina una suscripción de un usuario
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void cancelSubscription(Long userId);

  /**
   * Obtiene la suscripción de un usuario
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  SubscriptionResponseDTO getSubscriptionByUserId(Long userId);
}
