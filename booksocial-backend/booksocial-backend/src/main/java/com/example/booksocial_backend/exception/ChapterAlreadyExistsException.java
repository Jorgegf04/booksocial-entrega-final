package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta registrar un capitulo repetido dentro del mismo tomo.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class ChapterAlreadyExistsException extends RuntimeException {

  public ChapterAlreadyExistsException(Integer number, Long tomeId) {
    super("Ya existe un capítulo con número " + number + " en el tomo " + tomeId);
  }
}