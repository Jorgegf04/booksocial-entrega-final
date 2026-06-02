package com.example.booksocial_backend.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * Esta clase representa el cuerpo comun de las respuestas de error que devuelve
 * la api
 *
 * Centraliza la informacion que recibe el cliente cuando ocurre una excepcion:
 * fecha, codigo HTTP, mensaje, ruta afectada y, opcionalmente, errores por
 * campo.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Getter
public class ExceptionBody {

  private final LocalDateTime timestamp;
  private final int status;
  private final String message;
  private final String path;
  private final List<Map<String, String>> fieldErrors;

  /**
   * Construye una respuesta de error sin detalle de errores por campo.
   *
   * @param timestamp fecha y hora en la que se genera el error
   * @param status    codigo HTTP que representa el error
   * @param message   mensaje legible para el cliente
   * @param path      ruta de la peticion que produjo el error
   */
  public ExceptionBody(LocalDateTime timestamp, int status, String message, String path) {
    this.timestamp = timestamp;
    this.status = status;
    this.message = message;
    this.path = path;
    this.fieldErrors = null;
  }

  /**
   * Construye una respuesta de error con detalle de validaciones por campo.
   *
   * @param timestamp   fecha y hora en la que se genera el error
   * @param status      codigo HTTP que representa el error
   * @param message     mensaje legible para el cliente
   * @param path        ruta de la peticion que produjo el error
   * @param fieldErrors listado de errores de validacion asociados a campos
   *                    concretos
   */
  public ExceptionBody(LocalDateTime timestamp, int status, String message, String path,
      List<Map<String, String>> fieldErrors) {
    this.timestamp = timestamp;
    this.status = status;
    this.message = message;
    this.path = path;
    this.fieldErrors = fieldErrors;
  }
}
