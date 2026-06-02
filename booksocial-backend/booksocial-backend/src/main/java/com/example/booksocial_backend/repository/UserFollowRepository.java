package com.example.booksocial_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.domain.user.UserFollow;

/**
 * Repositorio de seguimientos entre usuarios de BookSocial.
 *
 * Maneja la persistencia de {@link UserFollow} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las relaciones sociales de seguimiento entre usuarios.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

  /**
   * Busca la relacion de seguimiento entre dos usuarios.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param follower usuario que realiza el seguimiento
   * @param following usuario que es seguido
   * @return Optional con la relacion encontrada, o vacio si no existe
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  Optional<UserFollow> findByFollowerAndFollowing(User follower, User following);

  /**
   * Obtiene los usuarios seguidos por un usuario concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param follower usuario que realiza el seguimiento
   * @return lista de relaciones donde el usuario actua como seguidor
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<UserFollow> findByFollower(User follower);

  /**
   * Obtiene los seguidores de un usuario concreto.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param following usuario que es seguido
   * @return lista de relaciones donde el usuario es seguido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<UserFollow> findByFollowing(User following);

  /**
   * Comprueba si existe una relacion de seguimiento entre dos usuarios.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * @param follower usuario que realiza el seguimiento
   * @param following usuario que es seguido
   * @return true si el seguimiento existe; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByFollowerAndFollowing(User follower, User following);
}