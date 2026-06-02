package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.WorkRequestDTO;
import com.example.booksocial_backend.DTO.catalog.WorkResponseDTO;

/**
 * Contrato de servicio para la gestion de obras.
 * Define operaciones de catalogo para crear, buscar, consultar, actualizar y
 * eliminar obras.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface WorkService {

  /**
   * Crea un nueva obra dentro de la aplicación
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos del recurso creado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  WorkResponseDTO createWork(WorkRequestDTO request);

  /**
   * Ejecuta una creacion masiva de obras.
   *
   * @param requests lista de DTOs con los datos necesarios para ejecutar la
   *                 operacion masiva
   * @return lista de DTOs de respuesta creados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<WorkResponseDTO> createMany(List<WorkRequestDTO> requests);

  /**
   * Obtiene un recurso por su ID.
   *
   * @param id identificador de la obra sobre el que se realiza la operacion
   * @return DTO de respuesta de la obra solicitado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  WorkResponseDTO getWorkById(Long id);

  /**
   * Obtiene todos las obras disponibles dentro del sistema
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<WorkResponseDTO> getAllWorks();

  /**
   * Busca obras aplicando filtros opcionales.
   *
   * @param title  titulo usado como filtro de busqueda
   * @param genre  genero usado como filtro de busqueda
   * @param rating valoracion minima usada como filtro de busqueda
   * @return lista de DTOs que cumplen los filtros
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<WorkResponseDTO> searchWorks(String title, String genre, Double rating);

  /**
   * Actualiza una obra existente con los datos recibidos.
   *
   * @param id      identificador de la obra sobre el que se realiza la operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  WorkResponseDTO updateWork(Long id, WorkRequestDTO request);

  /**
   * Elimina una obra dentro de la aplicación.
   *
   * @param id identificador de la obra sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteWork(Long id);
}
