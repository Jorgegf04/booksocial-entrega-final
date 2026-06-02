package com.example.booksocial_backend.controller.auth;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.booksocial_backend.DTO.auth.*;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.security.jwt.JwtUtils;
import com.example.booksocial_backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * Gestiona los endpoints de autenticación de BookSocial.
 * Permite registrar usuarios, iniciar sesión y generar tokens JWT para el
 * acceso seguro.
 * Centraliza la entrada pública al sistema de seguridad del backend.
 *
 * 
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "AuthController", description = "API REST para autenticación, registro y emisión de tokens JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final UserService userService;
  private final UserRepository userRepository;

  /**
   * Autentica las credenciales del usuario y devuelve un token JWT junto con los
   * datos básicos de sesión.
   * 
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Iniciar sesión", description = "Autentica las credenciales del usuario y devuelve un token JWT junto con los datos básicos de sesión.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Autenticacion correcta, token JWT emitido"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "Credenciales invalidas"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    String username = request.getUsername() != null ? request.getUsername().trim() : "";
    String password = request.getPassword() != null ? request.getPassword() : "";

    if (username.isEmpty() || password.isBlank()) {
      return ResponseEntity.badRequest()
          .body(Map.of("message", "Debes introducir el nombre de usuario y la contrase\u00f1a."));
    }

    if (!userRepository.existsByUsername(username)) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Esa cuenta no existe."));
    }

    Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              username,
              password));
    } catch (org.springframework.security.core.AuthenticationException e) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "La contrase\u00f1a introducida es incorrecta."));
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtUtils.generateJwtToken(authentication);

    com.example.booksocial_backend.security.service.UserDetailsImpl userDetails = (com.example.booksocial_backend.security.service.UserDetailsImpl) authentication
        .getPrincipal();

    String role = userDetails.getAuthorities().stream()
        .findFirst()
        .map(a -> a.getAuthority().replace("ROLE_", ""))
        .orElse("USER");

    return ResponseEntity.ok(new JwtResponse(jwt, "Bearer", userDetails.getId(), userDetails.getUsername(), role));
  }

  /**
   * Crea una nueva cuenta de usuario con los datos recibidos y confirma el
   * registro en la aplicación.
   * 
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario con los datos recibidos y confirma el registro en el sistema.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "409", description = "Usuario o email ya registrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

    userService.registerUser(request);

    return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
  }
}
