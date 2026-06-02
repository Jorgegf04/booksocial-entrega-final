package com.example.booksocial_backend.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.auth.RegisterRequest;
import com.example.booksocial_backend.DTO.user.CreateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UpdateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UserResponseDTO;
import com.example.booksocial_backend.domain.user.Role;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.domain.user.UserFollow;
import com.example.booksocial_backend.repository.UserFollowRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.exception.UserAlreadyExistsException;
import com.example.booksocial_backend.exception.UserNotFoundException;
import com.example.booksocial_backend.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de usuarios.
 *
 * Define operaciones de usuarios, registro y relaciones sociales entre usuarios. Coordina repositorios, validaciones de dominio y transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  /** Repositorio JPA utilizado para persistir, consultar y activar usuarios. */
  private final UserRepository userRepository;
  /** Componente de seguridad utilizado para cifrar contrasenas. */
  private final PasswordEncoder passwordEncoder;
  /** Repositorio JPA utilizado para gestionar relaciones de seguimiento entre usuarios. */
  private final UserFollowRepository userFollowRepository;

  // ==============================
  // CREATE
  // ==============================

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
  public UserResponseDTO createUser(CreateUserRequestDTO request) {

    validateCreateRequest(request);

    String username = request.getUsername().trim();
    String email = request.getEmail().trim().toLowerCase();

    log.info("[USER] [CREATE] [START] username='{}'", username);
    releaseInactiveCredentials(username, email);

    if (userRepository.existsByUsernameAndActiveTrue(username)) {
      log.warn("[USER] [CREATE] [CONFLICT] Username ya existe: '{}'", username);
      throw new UserAlreadyExistsException("El username '" + username + "' ya está en uso");
    }

    if (userRepository.existsByEmailAndActiveTrue(email)) {
      log.warn("[USER] [CREATE] [CONFLICT] Email ya existe: '{}'", email);
      throw new UserAlreadyExistsException("El email '" + email + "' ya está registrado");
    }

    User user = User.builder()
        .username(username)
        .email(email)
        .password(passwordEncoder.encode(request.getPassword()))
        .name(request.getName())
        .secondName(request.getSecondName())
        .registrationDate(LocalDate.now())
        .active(true)
        .role(request.getRole() != null ? request.getRole() : Role.REGISTERED)
        .build();

    return mapToDTO(userRepository.save(user));
  }

  // ==============================
  // READ
  // ==============================

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
  @Override
  @Transactional(readOnly = true)
  public UserResponseDTO getUserById(Long id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    return mapToDTO(user);
  }

  /**
   * Obtiene recursos asociados a un usuario.
   *
   * @param username valor utilizado por la operacion
   * @return lista de DTOs asociados al usuario indicado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public UserResponseDTO getUserByUsername(String username) {

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    return mapToDTO(user);
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
  public List<UserResponseDTO> getAllUsers() {

    return userRepository.findByActiveTrueOrderByIdAsc()
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  // ==============================
  // UPDATE
  // ==============================

  /**
   * Actualiza un recurso existente aplicando validaciones de negocio.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {

    if (request == null) throw new IllegalArgumentException("Datos de actualización inválidos");

    User existing = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));

    log.info("[USER] [UPDATE] [START] id={}", id);

    if (request.getUsername() != null && !request.getUsername().isBlank()) {
      String username = request.getUsername().trim();
      releaseInactiveCredentials(username, existing.getEmail());
      if (!existing.getUsername().equalsIgnoreCase(username) && userRepository.existsByUsernameAndActiveTrue(username)) {
        log.warn("[USER] [UPDATE] [CONFLICT] Username ya existe: '{}'", username);
        throw new UserAlreadyExistsException("El username '" + username + "' ya está en uso");
      }
      existing.setUsername(username);
    }

    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      String email = request.getEmail().trim().toLowerCase();
      releaseInactiveCredentials(existing.getUsername(), email);
      if (!existing.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmailAndActiveTrue(email)) {
        log.warn("[USER] [UPDATE] [CONFLICT] Email ya existe: '{}'", email);
        throw new UserAlreadyExistsException("El email '" + email + "' ya está registrado");
      }
      existing.setEmail(email);
    }

    if (request.getName() != null) existing.setName(request.getName());
    if (request.getSecondName() != null) existing.setSecondName(request.getSecondName());
    if (request.getImg() != null) existing.setImg(request.getImg());
    if (request.getRole() != null && !request.getRole().isBlank()) {
      try {
        existing.setRole(Role.valueOf(request.getRole().trim().toUpperCase()));
      } catch (IllegalArgumentException e) {
        log.warn("[USER] [UPDATE] Rol desconocido ignorado: '{}'", request.getRole());
      }
    }
    if (request.getActive() != null) existing.setActive(request.getActive());

    log.info("[USER] [UPDATE] [SUCCESS] id={}", id);
    return mapToDTO(userRepository.save(existing));
  }

  // ==============================
  // DELETE
  // ==============================

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
  public void deleteUser(Long id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));

    log.info("[USER] [DELETE] [START] id={} username='{}'", id, user.getUsername());
    user.setActive(false);
    releaseDeletedUserCredentials(user);
    userRepository.save(user);
    log.info("[USER] [DELETE] [SUCCESS] Borrado lógico aplicado a id={}", id);
  }

  // ==============================
  // MAPPER
  // ==============================

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param user valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private UserResponseDTO mapToDTO(User user) {

    return new UserResponseDTO(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getName(),
        user.getSecondName(),
        user.getImg(),
        user.getRegistrationDate(),
        user.getActive(),
        user.getRole());
  }

  // ==============================
  // VALIDATION
  // ==============================

  /**
   * Valida las reglas de negocio antes de continuar la operacion.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void validateCreateRequest(CreateUserRequestDTO request) {

    if (request == null) {
      throw new IllegalArgumentException("Datos de usuario inválidos");
    }

    if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("El username es obligatorio");
    }

    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("El email es obligatorio");
    }

    if (request.getPassword() == null || request.getPassword().length() < 6) {
      throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
    }
  }

  /**
   * Registra un usuario nuevo desde el flujo publico de autenticacion.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void registerUser(RegisterRequest request) {

    String username = request.getUsername().trim();
    String email = request.getEmail().trim().toLowerCase();

    releaseInactiveCredentials(username, email);

    if (userRepository.existsByUsernameAndActiveTrue(username)) {
      throw new UserAlreadyExistsException("El username '" + request.getUsername() + "' ya está en uso");
    }

    if (userRepository.existsByEmailAndActiveTrue(email)) {
      throw new UserAlreadyExistsException("El email '" + request.getEmail() + "' ya está registrado");
    }

    User user = new User();

    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setName(request.getName());
    user.setSecondName(request.getSecondName());
    user.setImg(request.getImg());
    user.setRegistrationDate(LocalDate.now());
    user.setActive(true);
    user.setRole(Role.REGISTERED);

    userRepository.save(user);
  }

  private void releaseInactiveCredentials(String username, String email) {
    List<User> inactiveConflicts = userRepository.findInactiveCredentialConflicts(username, email);
    if (inactiveConflicts == null || inactiveConflicts.isEmpty()) {
      return;
    }

    inactiveConflicts.forEach(this::releaseDeletedUserCredentials);
    userRepository.saveAll(inactiveConflicts);
  }

  private void releaseDeletedUserCredentials(User user) {
    String suffix = String.valueOf(user.getId() != null ? user.getId() : System.currentTimeMillis());
    user.setUsername(truncate("deleted_" + suffix + "_" + user.getUsername(), 50));
    user.setEmail(truncate("deleted_" + suffix + "_" + user.getEmail(), 100));
  }

  private String truncate(String value, int maxLength) {
    return value.length() <= maxLength ? value : value.substring(0, maxLength);
  }

  /**
   * Crea una relacion de seguimiento entre los recursos indicados.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @param targetId identificador del usuario objetivo de la relacion social
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void followUser(Long userId, Long targetId) {

    if (userId.equals(targetId)) {
      throw new IllegalArgumentException("No puedes seguirte a ti mismo");
    }

    User follower = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    User following = userRepository.findById(targetId)
        .orElseThrow(() -> new UserNotFoundException("Usuario objetivo no encontrado"));

    if (userFollowRepository.existsByFollowerAndFollowing(follower, following)) {
      throw new IllegalArgumentException("Ya sigues a este usuario");
    }

    UserFollow follow = UserFollow.builder()
        .follower(follower)
        .following(following)
        .followDate(java.time.LocalDateTime.now())
        .build();

    userFollowRepository.save(follow);
  }

  /**
   * Elimina, cancela o desactiva el recurso indicado segun la regla de negocio.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @param targetId identificador del usuario objetivo de la relacion social
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void unfollowUser(Long userId, Long targetId) {

    User follower = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    User following = userRepository.findById(targetId)
        .orElseThrow(() -> new UserNotFoundException("Usuario objetivo no encontrado"));

    UserFollow relation = userFollowRepository
        .findByFollowerAndFollowing(follower, following)
        .orElseThrow(() -> new IllegalArgumentException("No sigues a este usuario"));

    userFollowRepository.delete(relation);
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<UserResponseDTO> getFollowers(Long userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    return userFollowRepository.findByFollowing(user)
        .stream()
        .map(f -> mapToDTO(f.getFollower()))
        .toList();
  }

  /**
   * Obtiene informacion de negocio solicitada por el controlador.
   *
   * @param userId identificador del usuario asociado a la operacion
   * @return lista de resultados de la operacion
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<UserResponseDTO> getFollowing(Long userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    return userFollowRepository.findByFollower(user)
        .stream()
        .map(f -> mapToDTO(f.getFollowing()))
        .toList();
  }
}
