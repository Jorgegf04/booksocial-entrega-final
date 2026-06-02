package com.example.booksocial_backend.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksocial_backend.DTO.catalog.ChapterResponseDTO;
import com.example.booksocial_backend.DTO.catalog.ChapterRequestDTO;
import com.example.booksocial_backend.domain.catalog.Chapter;
import com.example.booksocial_backend.domain.catalog.Tome;
import com.example.booksocial_backend.exception.ChapterAlreadyExistsException;
import com.example.booksocial_backend.exception.ChapterNotFoundException;
import com.example.booksocial_backend.exception.TomeNotFoundException;
import com.example.booksocial_backend.repository.ChapterRepository;
import com.example.booksocial_backend.repository.TomeRepository;
import com.example.booksocial_backend.service.ChapterService;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion de servicio para la gestion de capitulos.
 * Define las operaciones necesarias para crear, listar y eliminar capitulos
 * asociados a tomos. Coordina repositorios, validaciones de dominio y
 * transformacion entre entidades y DTOs.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

  private static final Logger log = LoggerFactory.getLogger(ChapterServiceImpl.class);

  /** Repositorio JPA utilizado para persistir y consultar capitulos. */
  private final ChapterRepository chapterRepository;
  /** Repositorio JPA utilizado para validar el tomo asociado al capitulo. */
  private final TomeRepository tomeRepository;
  /** Componente utilizado para transformar DTOs en entidades de dominio. */
  private final ModelMapper modelMapper;

  /**
   * Crea un nuevo capitulo dentro de la aplicación
   * 
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del caitulo creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public ChapterResponseDTO createChapter(ChapterRequestDTO request) {

    log.info("[CHAPTER] [CREATE] [START] tomeId={} number={}", request.getTomeId(), request.getChapterNumber());
    Chapter chapter = modelMapper.map(request, Chapter.class);

    Tome tome = tomeRepository.findById(request.getTomeId())
        .orElseThrow(() -> new TomeNotFoundException(request.getTomeId()));

    chapter.setTome(tome);
    validateChapter(chapter);

    boolean exists = chapterRepository.findByTomeId(request.getTomeId()).stream()
        .anyMatch(c -> c.getChapterNumber().equals(request.getChapterNumber()));

    if (exists) {
      log.warn("[CHAPTER] [CREATE] [CONFLICT] Ya existe capítulo número={} en tomeId={}", request.getChapterNumber(),
          request.getTomeId());
      throw new ChapterAlreadyExistsException(request.getChapterNumber(), request.getTomeId());
    }

    Chapter saved = chapterRepository.save(chapter);
    log.info("[CHAPTER] [CREATE] [SUCCESS] id={} number={}", saved.getId(), saved.getChapterNumber());
    return mapToDTO(saved);
  }

  /**
   * Obtiene todos los capitulos dentro de la aplicación
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  @Transactional(readOnly = true)
  public List<ChapterResponseDTO> getAllChapters() {

    return chapterRepository.findAll()
        .stream()
        .map(this::mapToDTO)
        .toList();
  }

  /**
   * Elimina un capitulo dentro de la aplicación
   *
   * @param id identificador del rcapitulo sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  @Override
  public void deleteChapter(Long id) {

    log.info("[CHAPTER] [DELETE] [START] id={}", id);
    Chapter chapter = getChapterEntityById(id);
    chapterRepository.delete(chapter);
    log.info("[CHAPTER] [DELETE] [SUCCESS] id={}", id);
  }

  /**
   * Convierte una entidad de dominio en su DTO de respuesta.
   *
   * @param chapter valor utilizado por la operacion
   * @return DTO construido a partir de la entidad
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private ChapterResponseDTO mapToDTO(Chapter chapter) {
    Tome tome = chapter.getTome();
    return new ChapterResponseDTO(
        chapter.getId(),
        chapter.getChapterNumber(),
        chapter.getTitle(),
        tome != null ? tome.getId() : null,
        tome != null ? tome.getTitle() : null,
        (tome != null && tome.getEdition() != null) ? tome.getEdition().getTitle() : null);
  }

  /**
   * Obtiene un por su identificador.
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return DTO de respuesta del recurso solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private Chapter getChapterEntityById(Long id) {

    return chapterRepository.findById(id)
        .orElseThrow(() -> new ChapterNotFoundException(id));
  }

  /**
   * Valida las reglas de negocio antes de continuar la operacion.
   *
   * @param chapter valor utilizado por la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  private void validateChapter(Chapter chapter) {

    if (chapter == null) {
      throw new IllegalArgumentException("El capítulo no puede ser nulo");
    }

    if (chapter.getChapterNumber() == null || chapter.getChapterNumber() <= 0) {
      throw new IllegalArgumentException("El número de capítulo debe ser positivo");
    }

    if (chapter.getTitle() == null || chapter.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("El título del capítulo es obligatorio");
    }

    if (chapter.getTome() == null || chapter.getTome().getId() == null) {
      throw new IllegalArgumentException("El capítulo debe estar asociado a un tomo válido");
    }
  }
}
