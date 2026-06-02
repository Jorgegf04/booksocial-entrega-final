package com.example.booksocial_backend.controller.catalog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.catalog.ChapterRequestDTO;
import com.example.booksocial_backend.DTO.catalog.ChapterResponseDTO;
import com.example.booksocial_backend.service.ChapterService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona los capitulos asociados a tomos de manga.
 * Permite crear capitulos, consultar el listado disponible y eliminar registros cuando corresponde.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "ChapterController", description = "API REST para la gestion de capitulos")
@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

  private final ChapterService chapterService;

  /**
   * Endpoint que coordina la peticion REST con el servicio correspondiente.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "createChapter", description = "Ejecuta la operacion solicitada y devuelve la respuesta generada por el servicio.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<ChapterResponseDTO> createChapter(@Valid @RequestBody ChapterRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(chapterService.createChapter(request));
  }

  /**
   * Endpoint que sirve para crear varios recursos de golpe, util para cargas iniciales o pruebas desde Postman.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Creacion masiva", description = "Registra varios recursos en una sola peticion y devuelve el listado creado.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/batch")
  public ResponseEntity<List<ChapterResponseDTO>> createMany(
      @Valid @RequestBody List<ChapterRequestDTO> requests) {
    if (requests == null || requests.isEmpty()) {
      throw new IllegalArgumentException("La lista de capitulos no puede estar vacia");
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(chapterService::createChapter).toList());
  }

  /**
   * Endpoint que devuelve todos los recursos disponibles en el sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar recursos", description = "Devuelve el listado de recursos disponibles para consulta.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping
  public ResponseEntity<List<ChapterResponseDTO>> getAll() {
    return ResponseEntity.ok(chapterService.getAllChapters());
  }

  /**
   * Endpoint que sirve para eliminar un recurso del sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Eliminar recurso", description = "Elimina el recurso indicado y devuelve una respuesta sin contenido.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    chapterService.deleteChapter(id);
    return ResponseEntity.noContent().build();
  }
}
