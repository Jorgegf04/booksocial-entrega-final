package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de volumen desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class VolumeResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Numero del volumen dentro de la edicion. */
  private Integer volumeNumber;
  /** Titulo que se muestra en la vista. */
  private String title;
  /** Identificador de la edicion relacionada. */
  private Long editionId;
  /** Titulo de la edicion relacionada. */
  private String editionTitle;
}
