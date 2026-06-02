package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.social.CommentResponseDTO;
import com.example.booksocial_backend.DTO.social.CommentRequestDTO;

/**
 * Contrato de servicio para la gestion de comentarios.
 * Define las operaciones de comentarios, respuestas, actualizacion y
 * eliminacion dentro de obras.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface CommentService {

  /**
   * Crea un nuevo comentario dentro de la aplicación
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del comentario creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  CommentResponseDTO createComment(CommentRequestDTO request);

  /**
   * Para las respuestas de los comentarios
   *
   * @param parentId identificador del comentario padre
   * @param request  DTO con los datos necesarios para ejecutar la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  CommentResponseDTO replyToComment(Long parentId, CommentRequestDTO request);

  /**
   * Obtiene todos los recursos disponibles de comentarios dentro del sistema
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<CommentResponseDTO> getAllComments();

  /**
   * Obtiene la información necesaria para saber cual es el comentario razi de una
   * obra
   *
   * @param workId identificador de la obra asociada a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<CommentResponseDTO> getRootCommentsByWork(Long workId);

  /**
   * Elimina un comentario de una obra
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteComment(Long id);

  /**
   * Actualiza un recurso existente con los datos recibidos.
   *
   * @param id      identificador del recurso sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  CommentResponseDTO updateComment(Long id, CommentRequestDTO request);
}
