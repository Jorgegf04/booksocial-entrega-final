package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta eliminar una editorial que todavia tiene ediciones asociadas.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class EditorialHasEditionsException extends RuntimeException {
  public EditorialHasEditionsException(String name, int count) {
    super("No se puede eliminar la editorial '" + name + "' porque tiene " + count + " edición(es) asociada(s)");
  }
}
