package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Chapter;

/**
 * Repositorio de capitulos de BookSocial.
 *
 * Maneja la persistencia de {@link Chapter} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los capitulos asociados a tomos de obras tipo manga.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

  /**
   * Obtiene los capitulos asociados a un tomo concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param tomeId identificador del tomo usado como filtro
   * @return lista de capitulos del tomo indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Chapter> findByTomeId(Long tomeId);

}