package com.example.booksocial_backend.service;

import java.util.List;

import com.example.booksocial_backend.DTO.catalog.EditorialRequestDTO;
import com.example.booksocial_backend.DTO.catalog.EditorialResponseDTO;

/**
 * Contrato de servicio para la gestion de editoriales.
 * Define las operaciones de negocio para administrar editoriales del catalogo.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
public interface EditorialService {

  /**
   * Crea una nueva editorial dentro del sistema
   *
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos de la editorial creada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EditorialResponseDTO createEditorial(EditorialRequestDTO request);

  /**
   * Obtiene una editorial por su ID.
   *
   * @param id ID de la editorial sobre el que se realiza la operacion
   * @return DTO de respuesta de la editorial solicitada
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EditorialResponseDTO getEditorialById(Long id);

  /**
   * Obtiene todas las editoriales disponibles en la aplicación.
   *
   * @return lista de DTOs de respuesta
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  List<EditorialResponseDTO> getAllEditorials();

  /**
   * Actualiza una editorial existente con los datos recibidos.
   *
   * @param id      identificador de la editorial sobre el que se realiza la
   *                operacion
   * @param request DTO con los datos necesarios para ejecutar la operacion
   * @return DTO de respuesta con los datos actualizados
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  EditorialResponseDTO updateEditorial(Long id, EditorialRequestDTO request);

  /**
   * Elimina una editorial de la aplicación
   *
   * @param id identificador de la editorial sobre el que se realiza la operacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
  void deleteEditorial(Long id);
}
