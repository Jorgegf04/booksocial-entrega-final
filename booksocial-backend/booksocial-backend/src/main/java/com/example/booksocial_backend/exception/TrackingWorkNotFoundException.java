package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando no se encuentra el seguimiento de obra solicitado.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class TrackingWorkNotFoundException extends RuntimeException {
  public TrackingWorkNotFoundException(Long id) {
    super("Seguimiento de obra no encontrado con id: " + id);
  }
  public TrackingWorkNotFoundException(Long userId, Long workId) {
    super("Seguimiento no encontrado para usuario=" + userId + " y obra=" + workId);
  }
}
