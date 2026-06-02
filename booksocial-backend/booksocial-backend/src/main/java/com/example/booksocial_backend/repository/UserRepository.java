package com.example.booksocial_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.booksocial_backend.domain.user.User;

// Codigo de la ilustracion 31
//Codigo de la ilustracion 32
/**
 * Repositorio de usuarios de BookSocial.
 *
 * Maneja la persistencia de {@link User} mediante Spring Data JPA.
 * Hereda las operaciones CRUD de {@link JpaRepository} y declara consultas
 * derivadas o JPQL especificas para los casos de uso del backend.
 *
 * Gestiona las cuentas de usuario y sus datos unicos de acceso.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findByActiveTrueOrderByIdAsc();

  /**
   * Busca un usuario por su nombre de usuario.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * 
   * @param username nombre de usuario usado como criterio de busqueda
   * @return Optional con el usuario encontrado, o vacio si no existe
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  Optional<User> findByUsername(String username);

  /**
   * Comprueba si existe un usuario con el nombre de usuario indicado.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * 
   * @param username nombre de usuario usado como criterio de busqueda
   * @return true si el username ya existe; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByUsername(String username);

  boolean existsByUsernameAndActiveTrue(String username);

  /**
   * Comprueba si existe un usuario con el email indicado.
   *
   * Este metodo utiliza query derivada de Spring Data JPA a partir de su nombre.
   * 
   * @param email correo electronico usado como criterio de busqueda
   * @return true si el email ya existe; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean existsByEmail(String email);

  boolean existsByEmailAndActiveTrue(String email);

  @Query("""
      select u
      from User u
      where u.active = false
        and (lower(u.username) = lower(:username) or lower(u.email) = lower(:email))
      """)
  List<User> findInactiveCredentialConflicts(@Param("username") String username, @Param("email") String email);
}
