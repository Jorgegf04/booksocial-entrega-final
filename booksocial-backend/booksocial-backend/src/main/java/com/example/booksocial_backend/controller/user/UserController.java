package com.example.booksocial_backend.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.social.TrackingWorkResponseDTO;
import com.example.booksocial_backend.DTO.user.CreateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UpdateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UserResponseDTO;
import com.example.booksocial_backend.service.TrackingWorkService;
import com.example.booksocial_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

//REF-01/BACKEND/CONTROLLER/USERCONTROLLER.JAVA
/**
 * Gestiona los usuarios registrados dentro de BookSocial.
 * Permite administrar usuarios, consultar su biblioteca y gestionar relaciones
 * de seguimiento entre usuarios.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "UserController", description = "API REST para la gestion de usuarios y relaciones sociales")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final TrackingWorkService trackingWorkService;

  /**
   * Crea un usuario en el sistema y devuelve la respuesta con los datos
   * actualizados.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario y devuelve sus datos publicos.")
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
  public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
  }

  /**
   * Endpoint que sirve para crear varios recursos de golpe, util para cargas
   * iniciales o pruebas desde Postman.
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
  public ResponseEntity<List<UserResponseDTO>> createMany(@RequestBody List<CreateUserRequestDTO> requests) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(userService::createUser).toList());
  }

  /**
   * Endpoint que devuelve un usuario segun su ID.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Obtener usuario por ID", description = "Recupera un usuario concreto mediante su identificador.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  /**
   * Endpoint que devuelve todos los usuarios del sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar usuarios", description = "Devuelve el listado de usuarios disponibles para consulta.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping
  public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  /**
   * Endpoint que sirve para actualizar los datos de un usuario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente y devuelve la version actualizada.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
      @RequestBody UpdateUserRequestDTO request) {
    return ResponseEntity.ok(userService.updateUser(id, request));
  }

  /**
   * Endpoint que sirve para eliminar o desactivar un usuario del sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Eliminar usuario", description = "Aplica la eliminacion definida por el servicio y devuelve una respuesta sin contenido.")
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
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint que devuelve el seguimiento de obras de un usuario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Tracking de obras por usuario", description = "Recupera las obras guardadas o seguidas por el usuario indicado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}/tracking")
  public ResponseEntity<List<TrackingWorkResponseDTO>> getTrackingByUser(@PathVariable Long id) {
    return ResponseEntity.ok(trackingWorkService.getByUser(id));
  }

  /**
   * Endpoint que crea una relacion de seguimiento entre dos usuarios.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Seguir usuario", description = "Registra que un usuario comienza a seguir a otro.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{id}/follow/{targetId}")
  public ResponseEntity<String> followUser(@PathVariable Long id, @PathVariable Long targetId) {
    userService.followUser(id, targetId);
    return ResponseEntity.ok("Usuario seguido correctamente");
  }

  /**
   * Endpoint que elimina una relacion de seguimiento entre dos usuarios.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Dejar de seguir usuario", description = "Elimina la relacion de seguimiento entre dos usuarios.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}/follow/{targetId}")
  public ResponseEntity<String> unfollowUser(@PathVariable Long id, @PathVariable Long targetId) {
    userService.unfollowUser(id, targetId);
    return ResponseEntity.ok("Dejo de seguir correctamente");
  }

  /**
   * Endpoint que devuelve los seguidores de un usuario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Seguidores del usuario", description = "Recupera los usuarios que siguen al usuario indicado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}/followers")
  public ResponseEntity<List<UserResponseDTO>> getFollowers(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getFollowers(id));
  }

  /**
   * Endpoint que devuelve los usuarios seguidos por un usuario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Usuarios seguidos", description = "Recupera los usuarios a los que sigue el usuario indicado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/{id}/following")
  public ResponseEntity<List<UserResponseDTO>> getFollowing(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getFollowing(id));
  }
}
