package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para recibir datos de autor desde el backend y pintarlos en Thymeleaf.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class AuthorResponseDTO {
  /** Identificador unico del registro. */
  private Long id;
  /** Nombre principal que se muestra en la vista. */
  private String name;
  /** Nacionalidad del autor. */
  private String nationality;
  /** Fecha de nacimiento del autor. */
  private LocalDate birthDate;
  /** Ruta o URL de la imagen asociada. */
  private String img;
  /** Numero de seguidores asociados. */
  private Long followerCount;
  /** Lista de obras relacionadas. */
  private List<WorkResponseDTO> works;
}
