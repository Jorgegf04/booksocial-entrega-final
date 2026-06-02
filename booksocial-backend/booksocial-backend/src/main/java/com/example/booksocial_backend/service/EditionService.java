package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.EditionRequestDTO;
import com.example.booksocial_backend.DTO.catalog.EditionResponseDTO;

/**
 * Contrato de servicio para la gestion de ediciones.
 * Define las operaciones de negocio para crear, consultar, actualizar y
 * eliminar ediciones.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface EditionService {

  /**
   * Crea un nueva edicion dentro del sistema.
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos de la edicion creada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EditionResponseDTO createEdition(EditionRequestDTO request);

  /**
   * Obtiene una edicion por su ID.
   *
   * @param id ID de la edicion sobre el que se realiza la operacion
   * @return DTO de respuesta de la edicion solicitada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EditionResponseDTO getEditionById(Long id);

  /**
   * Obtiene todas las ediciones disponibles para este modulo.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<EditionResponseDTO> getAllEditions();

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
  EditionResponseDTO updateEdition(Long id, EditionRequestDTO request);

  /**
   * Elimina una edicion dentro de la aplicación
   *
   * @param id identificador del recurso sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteEdition(Long id);
}
