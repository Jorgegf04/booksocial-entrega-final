package com.example.booksocial_backend.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.domain.social.Comment;
import com.example.booksocial_backend.DTO.social.CommentResponseDTO;
import com.example.booksocial_backend.DTO.social.CommentRequestDTO;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.domain.user.User;

import com.example.booksocial_backend.exception.CommentNotFoundException;
import com.example.booksocial_backend.exception.UserNotFoundException;
import com.example.booksocial_backend.exception.WorkNotFoundException;
import com.example.booksocial_backend.repository.CommentRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.repository.WorkRepository;
import com.example.booksocial_backend.service.CommentService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de comentarios.
 *
 * Define las operaciones de comentarios, respuestas, actualizacion y eliminacion dentro de obras. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

  private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
  private static final int MAX_COMMENT_DEPTH = 4;

  /** Repositorio JPA utilizado para persistir y consultar comentarios. */
  private final CommentRepository commentRepository;
  /** Repositorio JPA utilizado para validar el usuario autor del comentario. */
  private final UserRepository userRepository;
  /** Repositorio JPA utilizado para validar la obra comentada. */
  private final WorkRepository workRepository;

  /**
   * Crea un nuevo recurso aplicando las validaciones de negocio correspondientes.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del recurso creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public CommentResponseDTO createComment(CommentRequestDTO request) {

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + request.getUserId()));

    Work work = workRepository.findById(request.getWorkId())
        .orElseThrow(() -> new WorkNotFoundException("Obra no encontrada con id: " + request.getWorkId()));

    Comment comment = new Comment();

    comment.setContent(request.getContent().trim());
    comment.setDate(LocalDateTime.now());

    comment.setUser(user);
    comment.setWork(work);

    comment.setParent(null);

    validateComment(comment);

    return mapToDTO(commentRepository.save(comment));
  }

  /**
   * Ejecuta la operacion de servicio replyToComment.
   *
   * @param parentId identificador del comentario padre
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return resultado de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public CommentResponseDTO replyToComment(Long parentId, CommentRequestDTO request) {

    Comment parent = getCommentEntityById(parentId);
    int parentDepth = calculateDepth(parent);

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + request.getUserId()));

    Work work = workRepository.findById(request.getWorkId())
        .orElseThrow(() -> new WorkNotFoundException("Obra no encontrada con id: " + request.getWorkId()));

    if (!parent.getWork().getId().equals(request.getWorkId())) {
      throw new IllegalArgumentException("La respuesta debe pertenecer a la misma obra");
    }

    if (parentDepth >= MAX_COMMENT_DEPTH) {
      throw new IllegalArgumentException("No se pueden crear respuestas con más de " + MAX_COMMENT_DEPTH + " niveles");
    }

    Comment reply = new Comment();

    reply.setContent(request.getContent().trim());
    reply.setDate(LocalDateTime.now());

    reply.setUser(user);
    reply.setWork(work);
    reply.setParent(parent);

    validateComment(reply);

    return mapToDTO(commentRepository.save(reply));
  }

  /**
   * Obtiene todos los recursos disponibles para este modulo.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<CommentResponseDTO> getAllComments() {

    return commentRepository.findAll()
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param workId identificador de la obra asociada a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<CommentResponseDTO> getRootCommentsByWork(Long workId) {

    return commentRepository.findByWorkIdAndParentIsNull(workId)
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Elimina, cancela o desactiva el recurso indicado segun la regla de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void deleteComment(Long id) {
    log.info("[COMMENT] [DELETE] [START] id={}", id);
    Comment comment = getCommentEntityById(id);
    commentRepository.delete(comment);
    log.info("[COMMENT] [DELETE] [SUCCESS] id={}", id);
  }

  /**
   * Convierte una entidad Comment a CommentDTO.
   *
   * Incluye únicamente un nivel de respuestas para evitar recursividad infinita.
   */
  private CommentResponseDTO mapToDTO(Comment comment) {
    return mapToDTOWithDepth(comment, 0);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param comment valor utilizado por la operacion
   * @param depth valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private CommentResponseDTO mapToDTOWithDepth(Comment comment, int depth) {
    return new CommentResponseDTO(
        comment.getId(),
        comment.getContent(),
        comment.getDate(),

        comment.getUpdatedAt(),
        comment.getEdited(),

        comment.getUser() != null ? comment.getUser().getId() : null,
        comment.getUser() != null ? comment.getUser().getUsername() : null,

        comment.getWork() != null ? comment.getWork().getId() : null,
        comment.getWork() != null ? comment.getWork().getTitle() : null,

        comment.getParent() != null ? comment.getParent().getId() : null,

        mapReplies(comment.getReplies(), depth + 1));
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param replies valor utilizado por la operacion
   * @param depth valor utilizado por la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private List<CommentResponseDTO> mapReplies(List<Comment> replies, int depth) {
    if (replies == null || replies.isEmpty() || depth >= MAX_COMMENT_DEPTH) {
      return List.of();
    }
    return replies.stream()
        .map(reply -> mapToDTOWithDepth(reply, depth))
        .toList();
  }

  private int calculateDepth(Comment comment) {
    int depth = 1;
    Comment current = comment;
    while (current.getParent() != null) {
      depth++;
      current = current.getParent();
    }
    return depth;
  }

  /**
   * Obtiene un recurso por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private Comment getCommentEntityById(Long id) {
    return commentRepository.findById(id)
        .orElseThrow(() -> new CommentNotFoundException(id));
  }

  /**
   * Valida los datos básicos de un comentario.
   */
  private void validateComment(Comment comment) {

    if (comment == null) {
      throw new IllegalArgumentException("El comentario no puede ser nulo");
    }

    if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("El contenido del comentario es obligatorio");
    }

    if (comment.getUser() == null || comment.getUser().getId() == null) {
      throw new IllegalArgumentException("El comentario debe estar asociado a un usuario válido");
    }

    if (comment.getWork() == null || comment.getWork().getId() == null) {
      throw new IllegalArgumentException("El comentario debe estar asociado a una obra válida");
    }
  }

  /**
   * Actualiza un recurso existente aplicando validaciones de negocio.
   *
   * @param commentId identificador del comentario asociado a la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO request) {

    Comment comment = getCommentEntityById(commentId);

    if (comment.getUser() == null || !comment.getUser().getId().equals(request.getUserId())) {
      throw new IllegalArgumentException("No tienes permiso para editar este comentario");
    }

    comment.setContent(request.getContent().trim());

    // Auditoría
    comment.setUpdatedAt(LocalDateTime.now());
    comment.setEdited(true);

    return mapToDTO(commentRepository.save(comment));
  }
}
