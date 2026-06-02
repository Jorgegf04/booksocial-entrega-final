package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.TomeRequestDTO;
import com.example.booksocial_backend.DTO.catalog.TomeResponseDTO;

/**
 * Contrato de servicio para la gestion de tomos.
 * Define operaciones de negocio para tomos asociados a ediciones de manga.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface TomeService {

  /**
   * Crea un nuevo tomo dentro de la aplicación. 
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del tomo creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  TomeResponseDTO createTome(TomeRequestDTO request);

  /**
   * Obtiene todos los tomos dentro de la aplicación.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<TomeResponseDTO> getAllTomes();

  /**
   * Actualiza un tomo existente con los datos recibidos.
   *
   * @param id identificador del tomo sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  TomeResponseDTO updateTome(Long id, TomeRequestDTO request);

  /**
   * Elimina un tomo dentro de la aplicación 
   *
   * @param id ID del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteTome(Long id);
}
