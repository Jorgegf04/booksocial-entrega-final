package com.example.booksocial_backend.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.example.booksocial_backend.DTO.catalog.TomeRequestDTO;
import com.example.booksocial_backend.domain.catalog.Edition;
import com.example.booksocial_backend.domain.catalog.Tome;
import com.example.booksocial_backend.domain.catalog.Work;
import com.example.booksocial_backend.repository.EditionRepository;
import com.example.booksocial_backend.repository.TomeRepository;
import com.example.booksocial_backend.service.impl.TomeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TomeServiceImplTest {

  @Mock
  private TomeRepository tomeRepository;

  @Mock
  private EditionRepository editionRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TomeServiceImpl service;

  private Tome tome;
  private Edition edition;

  @BeforeEach
  void setUp() {
    Work work = new Work();
    work.setTitle("Naruto");

    edition = new Edition();
    edition.setId(1L);
    edition.setTitle("Naruto Vol");
    edition.setWork(work);

    tome = new Tome();
    tome.setId(1L);
    tome.setNumberTome(1);
    tome.setTitle("Tomo 1");
    tome.setEdition(edition);
  }

  @Test
  void shouldCreateTomeSuccessfully() {
    TomeRequestDTO request = new TomeRequestDTO(1, 1L, "Tomo 1");

    when(modelMapper.map(request, Tome.class)).thenReturn(tome);
    when(tomeRepository.findByEditionId(1L)).thenReturn(List.of());
    when(tomeRepository.save(any())).thenReturn(tome);
    when(editionRepository.findById(1L)).thenReturn(Optional.of(edition));

    var result = service.createTome(request);

    assertEquals(1, result.getNumberTome());
    assertEquals("Tomo 1", result.getTitle());
  }

  @Test
  void shouldThrowExceptionWhenDuplicateTomeNumber() {
    TomeRequestDTO request = new TomeRequestDTO(1, 1L, "Tomo 1");

    when(modelMapper.map(request, Tome.class)).thenReturn(tome);
    when(tomeRepository.findByEditionId(1L)).thenReturn(List.of(tome));

    assertThrows(RuntimeException.class, () -> service.createTome(request));
  }

  @Test
  void shouldReturnAllTomes() {
    when(tomeRepository.findAll()).thenReturn(List.of(tome));

    var result = service.getAllTomes();

    assertEquals(1, result.size());
  }

  @Test
  void shouldUpdateTomeSuccessfully() {
    TomeRequestDTO request = new TomeRequestDTO(2, 1L, "Tomo 2");
    Tome updated = new Tome();
    updated.setNumberTome(2);
    updated.setEdition(edition);

    when(tomeRepository.findById(1L)).thenReturn(Optional.of(tome));
    when(modelMapper.map(request, Tome.class)).thenReturn(updated);
    when(tomeRepository.findByEditionId(1L)).thenReturn(List.of());
    when(tomeRepository.save(any())).thenReturn(tome);
    when(editionRepository.findById(1L)).thenReturn(Optional.of(edition));

    var result = service.updateTome(1L, request);

    assertEquals(2, result.getNumberTome());
  }

  @Test
  void shouldDeleteTome() {
    when(tomeRepository.findById(1L)).thenReturn(Optional.of(tome));
    when(editionRepository.findById(1L)).thenReturn(Optional.of(edition));
    when(tomeRepository.findByEditionId(1L)).thenReturn(List.of());

    service.deleteTome(1L);

    verify(tomeRepository).delete(tome);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistingTome() {
    when(tomeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.deleteTome(1L));
  }
}
