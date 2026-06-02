package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de evento desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class EventResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Titulo que se muestra en la vista. */
  private String title;
  /** Descripcion breve del elemento. */
  private String description;
  /** Ruta o URL de la imagen asociada. */
  private String img;
  /** Fecha en la que se creo o registro el dato. */
  private LocalDateTime date;
  /** Identificadores de usuarios relacionados. */
  private List<Long> userIds;
  /** Nombres de usuario relacionados. */
  private List<String> usernames;
  /** Numero total de participantes. */
  private Integer totalParticipants;
}
