/**
 * api/services/authorsService.js  Servicio CRUD de Autores.
 *
 * Consume los endpoints del backend:
 *   GET    /api/authors       → lista todos los autores
 *   POST   /api/authors       → crea un nuevo autor
 *   PUT    /api/authors/{id}  → actualiza un autor
 *   DELETE /api/authors/{id}  → elimina un autor
 */

import apiClient from '@/api/axios'

export const authorsService = {
  /**
   * Lista todos los autores del catálogo.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/authors')
    return Array.isArray(data) ? data : (data.content ?? [])
  },

  /**
   * Crea un nuevo autor.
   * @param {{ name, nationality, birthDate, img, workIds }} payload
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/authors', payload)
    return data
  },

  /**
   * Actualiza un autor existente.
   * @param {number} id
   * @param {{ name, nationality, birthDate, img, workIds }} payload
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/authors/${id}`, payload)
    return data
  },

  /**
   * Elimina un autor por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/authors/${id}`)
  },
}
