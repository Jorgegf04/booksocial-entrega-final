package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de pedido desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class OrderResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Fecha en la que se creo o registro el dato. */
  private LocalDateTime date;
  /** Dato de total usado por el DTO. */
  private Double total;
  /** Identificador del usuario relacionado. */
  private Long userId;
  /** Nombre de usuario usado para mostrar o iniciar sesion. */
  private String username;
  /** Correo del comprador invitado. */
  private String guestEmail;
  /** Cantidad total de productos del pedido. */
  private Integer totalItems;
  /** Lineas que forman parte del pedido. */
  private List<OrderLineResponseDTO> orderLines;
}
