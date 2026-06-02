package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta crear o actualizar un autor con un nombre que ya existe en el sistema.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class AuthorAlreadyExistsException extends RuntimeException {

  public AuthorAlreadyExistsException(String name) {
    super("Ya existe un autor con el nombre: " + name);
  }
}