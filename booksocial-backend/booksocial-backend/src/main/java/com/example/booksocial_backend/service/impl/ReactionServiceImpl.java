package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.domain.social.Reaction;
import com.example.booksocial_backend.DTO.social.ReactionResponseDTO;
import com.example.booksocial_backend.DTO.social.ReactionRequestDTO;
import com.example.booksocial_backend.domain.social.Comment;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.exception.ReactionNotFoundException;
import com.example.booksocial_backend.repository.ReactionRepository;
import com.example.booksocial_backend.service.ReactionService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de reacciones.
 *
 * Define operaciones para alternar, consultar y contar reacciones sobre comentarios. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReactionServiceImpl implements ReactionService {

  private static final Logger log = LoggerFactory.getLogger(ReactionServiceImpl.class);

  /** Repositorio JPA utilizado para consultar, crear y eliminar reacciones. */
  private final ReactionRepository reactionRepository;

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
  @Override
  public ReactionResponseDTO toggleReaction(ReactionRequestDTO request) {

    log.info("[REACTION] [TOGGLE] userId={} commentId={}", request.getUserId(), request.getCommentId());

    boolean exists = reactionRepository
        .existsByUserIdAndCommentId(request.getUserId(), request.getCommentId());

    // La reaccion funciona como interruptor: una segunda pulsacion elimina la existente.
    if (exists) {
      Reaction existing = reactionRepository
          .findByUserIdAndCommentId(request.getUserId(), request.getCommentId())
          .orElseThrow(() -> new ReactionNotFoundException(request.getUserId(), request.getCommentId()));

      String username = existing.getUser().getUsername();
      reactionRepository.delete(existing);
      log.info("[REACTION] [TOGGLE] [REMOVED] userId={} commentId={}", request.getUserId(), request.getCommentId());

      return new ReactionResponseDTO(null, null, request.getUserId(), username, request.getCommentId(), false);
    }

    Reaction reaction = Reaction.builder()
        .date(LocalDateTime.now())
        .user(User.builder().id(request.getUserId()).build())
        .comment(Comment.builder().id(request.getCommentId()).build())
        .build();

    Reaction saved = reactionRepository.save(reaction);
    log.info("[REACTION] [TOGGLE] [ADDED] id={} userId={} commentId={}", saved.getId(), request.getUserId(), request.getCommentId());

    return new ReactionResponseDTO(
        saved.getId(),
        saved.getDate(),
        saved.getUser().getId(),
        saved.getUser().getUsername(),
        saved.getComment().getId(),
        true);
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param commentId identificador del comentario asociado a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<ReactionResponseDTO> getReactionsByComment(Long commentId) {
    return reactionRepository.findByCommentId(commentId)
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Ejecuta la operacion de servicio countReactionsByComment.
   *
   * @param commentId identificador del comentario asociado a la operacion
   * @return valor numerico calculado por la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public int countReactionsByComment(Long commentId) {
    return reactionRepository.findByCommentId(commentId).size();
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param reaction valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private ReactionResponseDTO mapToDTO(Reaction reaction) {
    return new ReactionResponseDTO(
        reaction.getId(),
        reaction.getDate(),
        reaction.getUser().getId(),
        reaction.getUser().getUsername(),
        reaction.getComment().getId(),
        true);
  }
}
