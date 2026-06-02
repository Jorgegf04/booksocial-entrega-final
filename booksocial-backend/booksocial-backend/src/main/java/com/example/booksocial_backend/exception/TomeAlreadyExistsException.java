package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta registrar un tomo repetido dentro de la misma edicion.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class TomeAlreadyExistsException extends RuntimeException {
  public TomeAlreadyExistsException(Integer number, Long editionId) {
    super("Ya existe el tomo número " + number + " en la edición con id=" + editionId);
  }
}
