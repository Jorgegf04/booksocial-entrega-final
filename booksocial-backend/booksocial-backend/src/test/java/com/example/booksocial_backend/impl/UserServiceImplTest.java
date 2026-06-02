package com.example.booksocial_backend.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.booksocial_backend.DTO.user.CreateUserRequestDTO;
import com.example.booksocial_backend.DTO.user.UpdateUserRequestDTO;
import com.example.booksocial_backend.DTO.auth.RegisterRequest;
import com.example.booksocial_backend.domain.user.Role;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.domain.user.UserFollow;
import com.example.booksocial_backend.repository.UserFollowRepository;
import com.example.booksocial_backend.repository.UserRepository;
import com.example.booksocial_backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

//REF-08.BACKEND/SRC/TEST/JAVA/TEST/JAVA/IMPL/USERSERVICETEST.JAVA
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserFollowRepository userFollowRepository;

  @InjectMocks
  private UserServiceImpl service;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setUsername("jorge");
    user.setEmail("jorge@test.com");
    user.setName("Jorge");
    user.setSecondName("Garcia");
    user.setRegistrationDate(LocalDate.now());
    user.setActive(true);
    user.setRole(Role.REGISTERED);
  }

  @Test
  void shouldCreateUserSuccessfully() {
    CreateUserRequestDTO request = new CreateUserRequestDTO(
        "jorge", "password123", "jorge@test.com", "Jorge", "Garcia", Role.REGISTERED);

    when(userRepository.existsByUsernameAndActiveTrue("jorge")).thenReturn(false);
    when(userRepository.existsByEmailAndActiveTrue("jorge@test.com")).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("encoded");
    when(userRepository.save(any())).thenReturn(user);

    var result = service.createUser(request);

    assertNotNull(result);
    assertEquals("jorge", result.getUsername());
  }

  @Test
  void shouldThrowExceptionWhenUsernameAlreadyExists() {
    CreateUserRequestDTO request = new CreateUserRequestDTO(
        "jorge", "password123", "jorge@test.com", "Jorge", "Garcia", Role.REGISTERED);

    when(userRepository.existsByUsernameAndActiveTrue("jorge")).thenReturn(true);

    assertThrows(RuntimeException.class, () -> service.createUser(request));
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    CreateUserRequestDTO request = new CreateUserRequestDTO(
        "jorge", "password123", "jorge@test.com", "Jorge", "Garcia", Role.REGISTERED);

    when(userRepository.existsByUsernameAndActiveTrue("jorge")).thenReturn(false);
    when(userRepository.existsByEmailAndActiveTrue("jorge@test.com")).thenReturn(true);

    assertThrows(RuntimeException.class, () -> service.createUser(request));
  }

  @Test
  void shouldThrowExceptionWhenPasswordTooShort() {
    CreateUserRequestDTO request = new CreateUserRequestDTO(
        "jorge", "123", "jorge@test.com", "Jorge", "Garcia", Role.REGISTERED);

    assertThrows(IllegalArgumentException.class, () -> service.createUser(request));
  }

  @Test
  void shouldGetUserById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    var result = service.getUserById(1L);

    assertEquals(1L, result.getId());
    assertEquals("jorge", result.getUsername());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.getUserById(1L));
  }

  @Test
  void shouldGetAllUsers() {
    when(userRepository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of(user));

    var result = service.getAllUsers();

    assertEquals(1, result.size());
  }

  @Test
  void shouldUpdateUserSuccessfully() {
    UpdateUserRequestDTO request = new UpdateUserRequestDTO(
        "jorge_updated", "jorge_updated@test.com", "Jorge", "Garcia", null, null, null);

    User updated = new User();
    updated.setId(1L);
    updated.setUsername("jorge_updated");
    updated.setEmail("jorge_updated@test.com");
    updated.setName("Jorge");
    updated.setSecondName("Garcia");
    updated.setRegistrationDate(LocalDate.now());
    updated.setActive(true);
    updated.setRole(Role.REGISTERED);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.existsByUsernameAndActiveTrue("jorge_updated")).thenReturn(false);
    when(userRepository.existsByEmailAndActiveTrue("jorge_updated@test.com")).thenReturn(false);
    when(userRepository.save(any())).thenReturn(updated);

    var result = service.updateUser(1L, request);

    assertEquals("jorge_updated", result.getUsername());
  }

  @Test
  void shouldDeleteUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    service.deleteUser(1L);

    verify(userRepository).save(user);
    assertFalse(user.getActive());
    assertTrue(user.getUsername().startsWith("deleted_1_"));
    assertTrue(user.getEmail().startsWith("deleted_1_"));
  }

  @Test
  void shouldRegisterWithCredentialsFromInactiveUser() {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("jorge");
    request.setEmail("jorge@test.com");
    request.setPassword("password123");

    User inactive = new User();
    inactive.setId(9L);
    inactive.setUsername("jorge");
    inactive.setEmail("jorge@test.com");
    inactive.setActive(false);

    when(userRepository.findInactiveCredentialConflicts("jorge", "jorge@test.com")).thenReturn(List.of(inactive));
    when(userRepository.existsByUsernameAndActiveTrue("jorge")).thenReturn(false);
    when(userRepository.existsByEmailAndActiveTrue("jorge@test.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");

    service.registerUser(request);

    verify(userRepository).saveAll(List.of(inactive));
    verify(userRepository).save(any(User.class));
    assertTrue(inactive.getUsername().startsWith("deleted_9_"));
    assertTrue(inactive.getEmail().startsWith("deleted_9_"));
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistingUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.deleteUser(1L));
  }

  @Test
  void shouldFollowUserSuccessfully() {
    User target = new User();
    target.setId(2L);
    target.setUsername("pepe");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.findById(2L)).thenReturn(Optional.of(target));
    when(userFollowRepository.existsByFollowerAndFollowing(user, target)).thenReturn(false);

    service.followUser(1L, 2L);

    verify(userFollowRepository).save(any(UserFollow.class));
  }

  @Test
  void shouldThrowExceptionWhenFollowingSelf() {
    assertThrows(IllegalArgumentException.class, () -> service.followUser(1L, 1L));
  }

  @Test
  void shouldThrowExceptionWhenAlreadyFollowing() {
    User target = new User();
    target.setId(2L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.findById(2L)).thenReturn(Optional.of(target));
    when(userFollowRepository.existsByFollowerAndFollowing(user, target)).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> service.followUser(1L, 2L));
  }

  @Test
  void shouldUnfollowUserSuccessfully() {
    User target = new User();
    target.setId(2L);

    UserFollow follow = new UserFollow();
    follow.setFollower(user);
    follow.setFollowing(target);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.findById(2L)).thenReturn(Optional.of(target));
    when(userFollowRepository.findByFollowerAndFollowing(user, target)).thenReturn(Optional.of(follow));

    service.unfollowUser(1L, 2L);

    verify(userFollowRepository).delete(follow);
  }
}
