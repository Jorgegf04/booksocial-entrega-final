package com.example.booksocial_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.social.AuthorFollow;

/**
 * Repositorio de seguimientos de autores de BookSocial.
 *
 * Maneja la persistencia de {@link AuthorFollow} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las relaciones entre usuarios y autores seguidos.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface AuthorFollowRepository extends JpaRepository<AuthorFollow, Long> {

  /**
   * Comprueba si un usuario sigue a un autor concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param userId identificador del usuario usado como filtro
   * @param authorId identificador del autor usado como filtro
   * @return true si existe la relacion de seguimiento; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    boolean existsByUserIdAndAuthorId(Long userId, Long authorId);

  /**
   * Obtiene las obras o seguimientos asociados a un autor concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param authorId identificador del autor usado como filtro
   * @return lista de entidades asociadas al autor indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    List<AuthorFollow> findByAuthorId(Long authorId);

  /**
   * Cuenta cuantos usuarios siguen a un autor concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param authorId identificador del autor usado como filtro
   * @return numero total de seguimientos asociados al autor
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    long countByAuthorId(Long authorId);

  /**
   * Elimina mediante JPQL la relacion de seguimiento entre un usuario y un autor.
   *
   * Esta consulta utiliza JPQL declarado con {@link Query}.
   * @param userId identificador del usuario usado como filtro
   * @param authorId identificador del autor usado como filtro
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    @Modifying
    @Query("DELETE FROM AuthorFollow af WHERE af.user.id = :userId AND af.author.id = :authorId")
    void deleteByUserIdAndAuthorId(Long userId, Long authorId);

  /**
   * Elimina mediante JPQL todos los seguimientos asociados a un autor.
   *
   * Esta consulta utiliza JPQL declarado con {@link Query}.
   * @param authorId identificador del autor usado como filtro
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    @Modifying
    @Query("DELETE FROM AuthorFollow af WHERE af.author.id = :authorId")
    void deleteByAuthorId(Long authorId);
}
