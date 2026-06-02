package com.example.booksocial_backend.exception;

/**
 * Excepcion lanzada cuando un pedido solicita mas unidades de un producto que las disponibles en stock.
 *
 * Funciona como una excepcion de dominio no comprobada. Los servicios la lanzan
 * cuando detectan este caso y {@link GlobalExceptionHandler} la transforma en
 * una respuesta HTTP coherente para la API REST.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException(Long productId, int requested, int available) {
    super("Stock insuficiente para el producto con id " + productId
        + ": solicitado=" + requested + ", disponible=" + available);
  }

  public InsufficientStockException(String message) {
    super(message);
  }
}
