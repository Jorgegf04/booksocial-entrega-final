/**
 * api/services/chaptersService.js — Servicio CRUD de Capítulos.
 *
 * Consume los endpoints del backend:
 *   GET    /api/chapters       → lista todos los capítulos
 *   POST   /api/chapters       → crea un nuevo capítulo
 *   PUT    /api/chapters/{id}  → actualiza un capítulo
 *   DELETE /api/chapters/{id}  → elimina un capítulo
 */

import apiClient from '@/api/axios'

export const chaptersService = {
  /**
   * Lista todos los capítulos.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/chapters')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea un nuevo capítulo.
   * @param {Object} payload Datos del capítulo.
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/chapters', payload)
    return data
  },

  /**
   * Actualiza un capítulo existente.
   * @param {number} id
   * @param {Object} payload Datos actualizados del capítulo.
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/chapters/${id}`, payload)
    return data
  },

  /**
   * Elimina un capítulo por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/chapters/${id}`)
  },
}
