package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando una regla de validacion de negocio no se cumple.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}