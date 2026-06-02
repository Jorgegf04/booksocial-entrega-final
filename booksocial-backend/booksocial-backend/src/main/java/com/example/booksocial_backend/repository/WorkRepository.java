package com.example.booksocial_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.catalog.Work;

/**
 * Repositorio de obras de BookSocial.
 *
 * Maneja la persistencia de {@link Work} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las obras del catalogo y sus busquedas principales.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

  @EntityGraph(attributePaths = "authors")
  @Query("SELECT w FROM Work w")
  List<Work> findAllWithAuthors();

  @EntityGraph(attributePaths = "authors")
  @Query("SELECT w FROM Work w WHERE w.id = :id")
  Optional<Work> findByIdWithAuthors(Long id);

  /**
   * Obtiene las obras asociadas a un autor concreto.
   *
   * Esta consulta utiliza JPQL declarado con {@link Query}.
   * @param authorId identificador del autor usado como filtro
   * @return lista de obras relacionadas con el autor indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Query("SELECT w FROM Work w JOIN w.authors a WHERE a.id = :authorId")
  List<Work> findByAuthorId(Long authorId);

  /**
   * Ejecuta una busqueda JPQL de obras aplicando filtros opcionales.
   *
   * Permite filtrar por titulo parcial, genero y valoracion minima. Si un
   * parametro llega como {@code null}, la condicion correspondiente no limita
   * el resultado.
   *
   * Esta consulta utiliza JPQL declarado con {@link Query}.
   * @param title titulo parcial o completo usado como filtro opcional
   * @param genre genero usado como filtro opcional
   * @param rating valoracion minima usada como filtro opcional
   * @return lista de obras que cumplen los filtros proporcionados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Query("""
              SELECT w FROM Work w
              WHERE (:title IS NULL OR LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')))
              AND (:genre IS NULL OR LOWER(w.genre) = LOWER(:genre))
              AND (:rating IS NULL OR w.averageRating >= :rating)
          """)
  List<Work> searchWorks(String title, String genre, Double rating);

  @EntityGraph(attributePaths = "authors")
  @Query("""
              SELECT w FROM Work w
              WHERE (:title IS NULL OR LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')))
              AND (:genre IS NULL OR LOWER(w.genre) = LOWER(:genre))
              AND (:rating IS NULL OR w.averageRating >= :rating)
          """)
  List<Work> searchWorksWithAuthors(String title, String genre, Double rating);
}
