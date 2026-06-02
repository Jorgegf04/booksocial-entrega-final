/**
 * api/services/editionsService.js — Servicio CRUD de Ediciones.
 *
 * Consume los endpoints del backend:
 *   GET    /api/editions       → lista todas las ediciones
 *   POST   /api/editions       → crea una nueva edición
 *   PUT    /api/editions/{id}  → actualiza una edición
 *   DELETE /api/editions/{id}  → elimina una edición
 */

import apiClient from '@/api/axios'

export const editionsService = {
  /**
   * Lista todas las ediciones.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/editions')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea una nueva edición.
   * @param {Object} payload Datos de la edición.
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/editions', payload)
    return data
  },

  /**
   * Actualiza una edición existente.
   * @param {number} id
   * @param {Object} payload Datos actualizados de la edición.
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/editions/${id}`, payload)
    return data
  },

  /**
   * Elimina una edición por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/editions/${id}`)
  },
}
