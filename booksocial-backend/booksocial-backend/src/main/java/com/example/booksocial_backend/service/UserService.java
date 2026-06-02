package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.auth.RegisterRequest;
import com.example.booksocial_backend.DTO.user.CreateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UpdateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UserResponseDTO;

/**
 * Contrato de servicio para la gestion de usuarios.
 * Define operaciones de usuarios, registro y relaciones sociales entre
 * usuarios.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface UserService {

  /**
   * Crea un nuevo usuario dentro de la aplicación.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del usuario creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  UserResponseDTO createUser(CreateUserRequestDTO request);

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id ID del usuario sobre el que se realiza la operacion
   * @return DTO de respuesta del usuario solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  UserResponseDTO getUserById(Long id);

  /**
   * Obtiene un usuario por el nombre de usuario
   *
   * @param username valor utilizado por la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  UserResponseDTO getUserByUsername(String username);

  /**
   * Obtiene todos los usuarios disponibles en la aplicación.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<UserResponseDTO> getAllUsers();

  /**
   * Actualiza un usuario existente con los datos recibidos.
   *
   * @param id      ID del usuario sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request);

  /**
   * Elimina un usuario dentro de la aplicacion
   *
   * @param id identificador del usuario sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteUser(Long id);

  /**
   * Registra un usuario desde el flujo publico de autenticacion.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void registerUser(RegisterRequest request);

  /**
   * Crea una relación entre los usuarios
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param targetId identificador del usuario objetivo de la relacion social
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void followUser(Long userId, Long targetId);

  /**
   * Elimina la relación entre usuarios
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param targetId identificador del usuario objetivo de la relacion social
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void unfollowUser(Long userId, Long targetId);

  /**
   * Obtiene los seguidores de un usuario
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<UserResponseDTO> getFollowers(Long userId);

  /**
   * Obtiene los seguidos de un usuario
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<UserResponseDTO> getFollowing(Long userId);
}
