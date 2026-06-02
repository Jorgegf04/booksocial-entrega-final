package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.AuthorRequestDTO;
import com.example.booksocial_backend.DTO.catalog.AuthorResponseDTO;
import com.example.booksocial_backend.DTO.user.UserResponseDTO;

/**
 * Contrato de servicio para la gestion de autores.
 * Define las operaciones de negocio relacionadas con autores, seguimiento de
 * autores y consulta de seguidores.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface AuthorService {

  /**
   * Crea una nuevo autor dentro de la aplicación
   * 
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del autor creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  AuthorResponseDTO createAuthor(AuthorRequestDTO request);

  /**
   * Obtiene un autor segun su id correspondiente.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  AuthorResponseDTO getAuthorById(Long id);

  /**
   * Obtiene todos los autores disponibles en la aplicación.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<AuthorResponseDTO> getAllAuthors();

  /**
   * Actualiza un autor existente con los datos recibidos.
   *
   * @param id      identificador del autor sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO request);

  /**
   * Elimina un autor dentro de la aplicación.
   *
   * @param id identificador del autor sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteAuthor(Long id);

  /**
   * Crea una relacion de seguimiento entre los autores y los usuarios.
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param authorId identificador del autor asociado a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void followAuthor(Long userId, Long authorId);

  /**
   * Elimina la relación entre el usuario y el autor
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param authorId identificador del autor asociado a la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void unfollowAuthor(Long userId, Long authorId);

  /**
   * Comprueba si existe relación de seguimeito entre el autor y el usuario
   *
   * @param userId   identificador del usuario asociado a la operacion
   * @param authorId identificador del autor asociado a la operacion
   * @return true si la condicion se cumple; false en caso contrario
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  boolean isFollowing(Long userId, Long authorId);

  /**
   * Obtiene la cantidad de seguidores de un autor
   *
   * @param authorId identificador del autor asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<UserResponseDTO> getFollowers(Long authorId);
}
