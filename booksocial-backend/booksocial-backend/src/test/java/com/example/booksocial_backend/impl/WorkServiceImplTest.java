package com.example.booksocial_backend.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.booksocial_backend.DTO.catalog.WorkRequestDTO;
import com.example.booksocial_backend.domain.catalog.Author;
import com.example.booksocial_backend.domain.catalog.Genre;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.domain.catalog.WorkType;
import com.example.booksocial_backend.domain.social.AuthorFollow;
import com.example.booksocial_backend.domain.user.User;
import com.example.booksocial_backend.repository.AuthorFollowRepository;
import com.example.booksocial_backend.repository.AuthorRepository;
import com.example.booksocial_backend.repository.WorkRepository;
import com.example.booksocial_backend.service.EmailService;
import com.example.booksocial_backend.service.impl.WorkServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class WorkServiceImplTest {

  @Mock
  private WorkRepository workRepository;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private AuthorRepository authorRepository;

  @Mock
  private AuthorFollowRepository authorFollowRepository;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private WorkServiceImpl service;

  private Work work;
  private Author author;

  @BeforeEach
  void setUp() {
    author = new Author();
    author.setId(1L);
    author.setName("Masashi Kishimoto");

    work = new Work();
    work.setId(1L);
    work.setTitle("Naruto");
    work.setGenre(Genre.ACTION);
    work.setAuthors(new ArrayList<>(List.of(author)));
  }

  @Test
  void shouldCreateWorkSuccessfully() {
    WorkRequestDTO request = new WorkRequestDTO(
        "Naruto", "Descripcion", Genre.ACTION, WorkType.MANGA, null, null, null, null, List.of("Masashi Kishimoto"));

    when(modelMapper.map(request, Work.class)).thenReturn(work);
    when(authorRepository.findByNameIgnoreCase("Masashi Kishimoto")).thenReturn(Optional.of(author));
    when(workRepository.save(any())).thenReturn(work);
    when(authorFollowRepository.findByAuthorId(anyLong())).thenReturn(List.of());

    var result = service.createWork(request);

    assertNotNull(result);
    assertEquals("Naruto", result.getTitle());
  }

  @Test
  void shouldNotifyAuthorFollowersWhenCreatingWork() {
    WorkRequestDTO request = new WorkRequestDTO(
        "Naruto", "Descripcion", Genre.ACTION, WorkType.MANGA, null, null, null, null, List.of("Masashi Kishimoto"));
    User follower = User.builder()
        .id(7L)
        .username("reader")
        .email("reader@example.com")
        .build();
    AuthorFollow follow = AuthorFollow.builder()
        .user(follower)
        .author(author)
        .build();

    when(modelMapper.map(request, Work.class)).thenReturn(work);
    when(authorRepository.findByNameIgnoreCase("Masashi Kishimoto")).thenReturn(Optional.of(author));
    when(workRepository.save(any())).thenReturn(work);
    when(authorFollowRepository.findByAuthorId(1L)).thenReturn(List.of(follow));

    service.createWork(request);

    verify(emailService).sendNewWorkNotification("reader@example.com", "Masashi Kishimoto", "Naruto");
  }

  @Test
  void shouldThrowExceptionWhenTitleIsNull() {
    WorkRequestDTO request = new WorkRequestDTO(
        null, "Descripcion", Genre.ACTION, WorkType.MANGA, null, null, null, null, null);

    assertThrows(IllegalArgumentException.class, () -> service.createWork(request));
  }

  @Test
  void shouldThrowExceptionWhenGenreIsNull() {
    WorkRequestDTO request = new WorkRequestDTO(
        "Naruto", "Descripcion", null, WorkType.MANGA, null, null, null, null, null);

    assertThrows(IllegalArgumentException.class, () -> service.createWork(request));
  }

  @Test
  void shouldThrowExceptionWhenAuthorNotFound() {
    WorkRequestDTO request = new WorkRequestDTO(
        "Naruto", null, Genre.ACTION, WorkType.MANGA, null, null, null, null, List.of("Autor Desconocido"));

    when(modelMapper.map(request, Work.class)).thenReturn(work);
    when(authorRepository.findByNameIgnoreCase("Autor Desconocido")).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.createWork(request));
  }

  @Test
  void shouldGetWorkById() {
    when(workRepository.findByIdWithAuthors(1L)).thenReturn(Optional.of(work));

    var result = service.getWorkById(1L);

    assertEquals(1L, result.getId());
    assertEquals("Naruto", result.getTitle());
  }

  @Test
  void shouldThrowExceptionWhenWorkNotFound() {
    when(workRepository.findByIdWithAuthors(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.getWorkById(1L));
  }

  @Test
  void shouldGetAllWorks() {
    when(workRepository.findAllWithAuthors()).thenReturn(List.of(work));

    var result = service.getAllWorks();

    assertEquals(1, result.size());
    assertEquals("Naruto", result.get(0).getTitle());
  }

  @Test
  void shouldUpdateWorkSuccessfully() {
    WorkRequestDTO request = new WorkRequestDTO(
        "Naruto Shippuden", null, null, null, null, null, null, null, null);

    when(workRepository.findByIdWithAuthors(1L)).thenReturn(Optional.of(work));
    when(workRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = service.updateWork(1L, request);

    assertEquals("Naruto Shippuden", result.getTitle());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistingWork() {
    WorkRequestDTO request = new WorkRequestDTO(
        "Naruto Shippuden", null, null, null, null, null, null, null, null);

    when(workRepository.findByIdWithAuthors(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.updateWork(1L, request));
  }

  @Test
  void shouldDeleteWork() {
    when(workRepository.findByIdWithAuthors(1L)).thenReturn(Optional.of(work));

    service.deleteWork(1L);

    verify(workRepository).delete(work);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistingWork() {
    when(workRepository.findByIdWithAuthors(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.deleteWork(1L));
  }

  @Test
  void shouldThrowExceptionWhenCreateManyEmpty() {
    assertThrows(IllegalArgumentException.class, () -> service.createMany(List.of()));
  }
}
