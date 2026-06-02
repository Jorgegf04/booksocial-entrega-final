package com.example.booksocial_backend.controller.social;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.social.ReactionRequestDTO;
import com.example.booksocial_backend.DTO.social.ReactionResponseDTO;
import com.example.booksocial_backend.service.ReactionService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona las reacciones de usuarios sobre comentarios.
 * Permite alternar reacciones, consultar las asociadas a un comentario y contar el total.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "ReactionController", description = "API REST para la gestion de reacciones")
@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

  private final ReactionService reactionService;

  /**
   * Endpoint que alterna la reaccion de un usuario sobre un comentario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Alternar reaccion", description = "Crea o elimina la reaccion segun el estado actual del usuario sobre el comentario.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<ReactionResponseDTO> toggle(@Valid @RequestBody ReactionRequestDTO request) {
    return ResponseEntity.ok(reactionService.toggleReaction(request));
  }

  /**
   * Endpoint que devuelve las reacciones asociadas a un comentario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Reacciones por comentario", description = "Recupera las reacciones registradas sobre el comentario indicado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/comment/{commentId}")
  public ResponseEntity<List<ReactionResponseDTO>> getByComment(@PathVariable Long commentId) {
    return ResponseEntity.ok(reactionService.getReactionsByComment(commentId));
  }

  /**
   * Endpoint que coordina la peticion REST con el servicio correspondiente.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "count", description = "Ejecuta la operacion solicitada y devuelve la respuesta generada por el servicio.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/comment/{commentId}/count")
  public ResponseEntity<Integer> count(@PathVariable Long commentId) {
    return ResponseEntity.ok(reactionService.countReactionsByComment(commentId));
  }
}
