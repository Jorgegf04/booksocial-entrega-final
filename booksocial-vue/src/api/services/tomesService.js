/**
 * api/services/tomesService.js — Servicio CRUD de Tomos.
 *
 * Consume los endpoints del backend:
 *   GET    /api/tomes       → lista todos los tomos
 *   POST   /api/tomes       → crea un nuevo tomo
 *   PUT    /api/tomes/{id}  → actualiza un tomo
 *   DELETE /api/tomes/{id}  → elimina un tomo
 */

import apiClient from '@/api/axios'

export const tomesService = {
  /**
   * Lista todos los tomos.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/tomes')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea un nuevo tomo.
   * @param {Object} payload Datos del tomo.
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/tomes', payload)
    return data
  },

  /**
   * Actualiza un tomo existente.
   * @param {number} id
   * @param {Object} payload Datos actualizados del tomo.
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/tomes/${id}`, payload)
    return data
  },

  /**
   * Elimina un tomo por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/tomes/${id}`)
  },
}
