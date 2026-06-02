package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.VolumeRequestDTO;
import com.example.booksocial_backend.DTO.catalog.VolumeResponseDTO;

/**
 * Contrato de servicio para la gestion de volumenes.
 * Define operaciones de negocio para volumenes asociados a ediciones de comic.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface VolumeService {

  /**
   * Crea un nuevo volumen dentro de la aplicaciópn
   * 
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del volumenes creados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  VolumeResponseDTO createVolume(VolumeRequestDTO request);

  /**
   * Obtiene todos los volumenes disponibles para este modulo.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<VolumeResponseDTO> getAllVolumes();

  /**
   * Actualiza un volumen existente con los datos recibidos.
   *
   * @param id      identificador del volumen sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  VolumeResponseDTO updateVolume(Long id, VolumeRequestDTO request);

  /**
   * Elimina un volumen dentro de la aplicación
   *
   * @param id identificador del volumen sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteVolume(Long id);
}
