package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta registrar una edicion con un ISBN ya existente.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class EditionAlreadyExistsException extends RuntimeException {
  public EditionAlreadyExistsException(String isbn) {
    super("Ya existe una edición con el ISBN: " + isbn);
  }
}
