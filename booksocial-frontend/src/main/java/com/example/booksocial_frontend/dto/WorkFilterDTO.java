package com.example.booksocial_frontend.dto;

import java.time.LocalDate;

import java.util.List;

import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado para guardar los filtros del catalogo de obras antes de buscar resultados.
 * Las anotaciones de Lombok generan getters, setters y constructores segun se necesite.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Data
@NoArgsConstructor
public class WorkFilterDTO {

  /** Titulo que se muestra en la vista. */
  private String title;
  /** Generos seleccionados para filtrar obras. */
  private List<Genre> genres;
  /** Tipo o categoria usada por la obra o el token. */
  private WorkType type;
  /** Demografia a la que va dirigida la obra. */
  private Demographic demographic;
  /** Valoracion minima usada en el filtro. */
  private Double minRating;
  /** Fecha minima de publicacion para filtrar. */
  private LocalDate publishedAfter;
  /** Fecha maxima de publicacion para filtrar. */
  private LocalDate publishedBefore;
  /** Identificador del autor usado en filtros. */
  private Long authorId;
  /** Opcion de ordenacion elegida. */
  private String sort;

}
