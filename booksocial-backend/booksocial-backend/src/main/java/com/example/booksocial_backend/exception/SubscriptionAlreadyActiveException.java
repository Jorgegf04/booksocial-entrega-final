package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta activar una suscripcion para un usuario que ya tiene una activa.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class SubscriptionAlreadyActiveException extends RuntimeException {
  public SubscriptionAlreadyActiveException(Long userId) {
    super("El usuario con id=" + userId + " ya tiene una suscripción activa");
  }
}
