package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando un usuario intenta ejecutar una accion para la que no tiene permisos.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class UnauthorizedActionException extends RuntimeException {

  public UnauthorizedActionException(String message) {
    super(message);
  }
}
