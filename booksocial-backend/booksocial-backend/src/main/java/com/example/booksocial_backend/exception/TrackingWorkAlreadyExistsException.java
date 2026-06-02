package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando un usuario intenta registrar dos veces el seguimiento de la misma obra.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class TrackingWorkAlreadyExistsException extends RuntimeException {
  public TrackingWorkAlreadyExistsException(Long userId, Long workId) {
    super("El usuario con id=" + userId + " ya sigue la obra con id=" + workId);
  }
}
