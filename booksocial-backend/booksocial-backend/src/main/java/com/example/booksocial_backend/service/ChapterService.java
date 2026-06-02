package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.ChapterRequestDTO;
import com.example.booksocial_backend.DTO.catalog.ChapterResponseDTO;

/**
 * Contrato de servicio para la gestion de capitulos.
 * Define las operaciones necesarias para crear, listar y eliminar capitulos
 * asociados a tomos del catalogo.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface ChapterService {

  /**
   * Crea un nuevo capitulo dentro de un tomo.
   *
   * @param request DTO con los datos del capitulo y el tomo asociado
   * @return DTO de respuesta con el capitulo creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  ChapterResponseDTO createChapter(ChapterRequestDTO request);

  /**
   * Obtiene todos los capitulos del sistema.
   *
   * @return lista de capitulos disponibles
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<ChapterResponseDTO> getAllChapters();

  /**
   * Elimina un capitulo del sistema.
   *
   * @param id identificador del capitulo que se va a eliminar
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteChapter(Long id);
}
