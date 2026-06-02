package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.social.ReactionResponseDTO;
import com.example.booksocial_backend.DTO.social.ReactionRequestDTO;

/**
 * Contrato de servicio para la gestion de reacciones.
 * Define operaciones para alternar, consultar y contar reacciones sobre
 * comentarios.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface ReactionService {

  /**
   * Alterna la reaccion de un usuario sobre un comentario.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con el estado final de la reaccion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  ReactionResponseDTO toggleReaction(ReactionRequestDTO request);

  /**
   * Obtiene los comentarios asociados a una reacción
   *
   * @param commentId identificador del comentario asociado a la operacion
   * @return resultado solicitado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<ReactionResponseDTO> getReactionsByComment(Long commentId);

  /**
   * Ejecuta la operacion de servicio countReactionsByComment.
   *
   * @param commentId identificador del comentario asociado a la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  int countReactionsByComment(Long commentId);
}
