package com.example.booksocial_backend.controller.catalog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.booksocial_backend.DTO.catalog.AuthorRequestDTO;
import com.example.booksocial_backend.DTO.catalog.AuthorResponseDTO;
import com.example.booksocial_backend.DTO.user.UserResponseDTO;
import com.example.booksocial_backend.service.AuthorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona los autores disponibles dentro de la aplicación.
 * Permite crear, consultar, buscar, ordenar y eliminar autores.
 * También gestiona las operaciones necesarias para seguir a los autores
 * 
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "AuthorController", description = "API REST para la gestión completa de autores y seguidores")
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  // CREATE

  /**
   * Crea un autor en el sistema y devuelve el recurso la respuesta con los datos
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Crear autor", description = "Registra autor en el sistema y devuelve el recurso creado con sus datos actualizados.")
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
  public ResponseEntity<AuthorResponseDTO> createAuthor(@Valid @RequestBody AuthorRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(request));
  }

  /**
   * Endpoint que sirve para crear varios autores de golpe, perfecto para pasarle
   * un json en postman, asi se han creado los datos
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Creación masiva de autores", description = "Registra varios recursos en una sola petición y devuelve el listado creado por el servicio.")
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
  public ResponseEntity<List<AuthorResponseDTO>> createMany(@Valid @RequestBody List<AuthorRequestDTO> requests) {
    if (requests == null || requests.isEmpty())
      throw new IllegalArgumentException("La lista de autores no puede estar vacía");
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(authorService::createAuthor).toList());
  }

  // READ

  /**
   * Endpoint que devuelve una obra segun su ID
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Obtener autor por ID", description = "Recupera un recurso concreto mediante su identificador y devuelve sus datos completos.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}")
  public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable Long id) {
    return ResponseEntity.ok(authorService.getAuthorById(id));
  }

  /**
   * Edpoint que devuelve todas las obras de sistema
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar todos los autores", description = "Devuelve el listado de recursos disponibles para su consulta desde el cliente.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping
  public ResponseEntity<List<AuthorResponseDTO>> getAllAuthors() {
    return ResponseEntity.ok(authorService.getAllAuthors());
  }

  // UPDATE / DELETE

  /**
   * Endpint que sirve para actualizar los datos de un autor y la respusta deuelve
   * los datos actuaizados del autor
   * 
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Actualizar autor", description = "Modifica los datos o el estado de un recurso existente y devuelve la versión actualizada.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<AuthorResponseDTO> updateAuthor(@PathVariable Long id,
      @Valid @RequestBody AuthorRequestDTO request) {
    return ResponseEntity.ok(authorService.updateAuthor(id, request));
  }

  /**
   * Endpoint que sirve para eliminar un autor del sistema
   * 
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Eliminar autor", description = "Elimina o cancela el recurso indicado y devuelve una respuesta sin contenido cuando finaliza correctamente.")
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
  public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
    authorService.deleteAuthor(id);
    return ResponseEntity.noContent().build();
  }

  // FOLLOW — Esto requiere autenticación (solo los usuarios registrados pueden
  // seguir autores)

  /**
   * Este endpoint lo que hace es que crea una relación entre el usuario y el
   * autor para registrar el seguimiento
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Seguir autor", description = "Crea una relación entre el usuario y el recurso indicado para registrar la acción solicitada.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{id}/follow")
  public ResponseEntity<Void> followAuthor(@PathVariable Long id, @RequestParam Long userId) {
    authorService.followAuthor(userId, id);
    return ResponseEntity.ok().build();
  }

  /**
   * Este endpoint lo que hace es eliminar la relación entre el usuario y el autor
   * para cancelar el seguimiento
   * 
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Dejar de seguir autor", description = "Elimina la relación entre el usuario y el recurso indicado para deshacer la acción previa.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}/follow")
  public ResponseEntity<Void> unfollowAuthor(@PathVariable Long id, @RequestParam Long userId) {
    authorService.unfollowAuthor(userId, id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint que verifica si el x usuario sigue a x autor para controlarlo
   * 
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "¿El usuario sigue a este autor?", description = "Ejecuta la operación '¿El usuario sigue a este autor?' y devuelve la respuesta generada por el servicio correspondiente.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}/following")
  public ResponseEntity<Boolean> isFollowing(@PathVariable Long id, @RequestParam Long userId) {
    return ResponseEntity.ok(authorService.isFollowing(userId, id));
  }

  /**
   * Endpint que sirve para saber los seguidores de un autor, puede ser util para
   * hacer estadísticas sobre la web
   * 
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Seguidores del autor", description = "Ejecuta la operación 'Seguidores del autor' y devuelve la respuesta generada por el servicio correspondiente.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}/followers")
  public ResponseEntity<List<UserResponseDTO>> getFollowers(@PathVariable Long id) {
    return ResponseEntity.ok(authorService.getFollowers(id));
  }
}
