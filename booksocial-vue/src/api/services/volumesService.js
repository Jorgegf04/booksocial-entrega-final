/**
 * api/services/volumesService.js — Servicio CRUD de Volúmenes.
 *
 * Consume los endpoints del backend:
 *   GET    /api/volumes       → lista todos los volúmenes
 *   POST   /api/volumes       → crea un nuevo volumen
 *   PUT    /api/volumes/{id}  → actualiza un volumen
 *   DELETE /api/volumes/{id}  → elimina un volumen
 */

import apiClient from '@/api/axios'

export const volumesService = {
  /**
   * Lista todos los volúmenes.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/volumes')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea un nuevo volumen.
   * @param {Object} payload Datos del volumen.
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/volumes', payload)
    return data
  },

  /**
   * Actualiza un volumen existente.
   * @param {number} id
   * @param {Object} payload Datos actualizados del volumen.
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/volumes/${id}`, payload)
    return data
  },

  /**
   * Elimina un volumen por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/volumes/${id}`)
  },
}
