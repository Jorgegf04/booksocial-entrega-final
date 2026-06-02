package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.social.Comment;

/**
 * Repositorio de comentarios de BookSocial.
 *
 * Maneja la persistencia de {@link Comment} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona los comentarios publicados sobre obras y sus respuestas.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  /**
   * Obtiene los comentarios raiz de una obra, excluyendo respuestas.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param workId identificador de la obra usada como filtro
   * @return lista de comentarios principales asociados a la obra
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<Comment> findByWorkIdAndParentIsNull(Long workId);
}
