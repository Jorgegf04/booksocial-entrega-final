package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando no se encuentra el usuario solicitado.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("Usuario no encontrado con id: " + id);
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
