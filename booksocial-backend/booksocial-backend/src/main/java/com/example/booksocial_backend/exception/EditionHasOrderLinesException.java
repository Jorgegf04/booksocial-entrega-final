package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando se intenta eliminar una edicion que ya tiene lineas de pedido asociadas.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class EditionHasOrderLinesException extends RuntimeException {
  public EditionHasOrderLinesException(Long editionId) {
    super("No se puede eliminar la edición con id=" + editionId + " porque tiene pedidos históricos asociados. Elimine primero los pedidos o desasocie los productos.");
  }
}
