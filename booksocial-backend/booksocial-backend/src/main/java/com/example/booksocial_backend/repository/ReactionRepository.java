package com.example.booksocial_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.social.Reaction;

/**
 * Repositorio de reacciones de BookSocial.
 *
 * Maneja la persistencia de {@link Reaction} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las reacciones de usuarios sobre comentarios.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

  /**
   * Obtiene las reacciones asociadas a un comentario.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param commentId identificador del comentario usado como filtro
   * @return lista de reacciones del comentario indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Reaction> findByCommentId(Long commentId);

  /**
   * Busca la reaccion de un usuario sobre un comentario concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param userId identificador del usuario usado como filtro
   * @param commentId identificador del comentario usado como filtro
   * @return Optional con la reaccion encontrada, o vacio si no existe
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  Optional<Reaction> findByUserIdAndCommentId(Long userId, Long commentId);

  /**
   * Comprueba si un usuario ya reacciono a un comentario.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param userId identificador del usuario usado como filtro
   * @param commentId identificador del comentario usado como filtro
   * @return true si existe la reaccion; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByUserIdAndCommentId(Long userId, Long commentId);

}