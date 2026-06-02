/**
 * api/services/editorialsService.js — Servicio CRUD de Editoriales.
 *
 * Consume los endpoints del backend:
 *   GET    /api/editorials       → lista todas las editoriales
 *   POST   /api/editorials       → crea una editorial
 *   PUT    /api/editorials/{id}  → actualiza una editorial
 *   DELETE /api/editorials/{id}  → elimina una editorial
 *
 * Es la entidad más simple: solo name y country, sin imágenes.
 */

import apiClient from '@/api/axios'

export const editorialsService = {
  /**
   * Lista todas las editoriales.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/editorials')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea una nueva editorial.
   * @param {{ name: string, country: string }} payload
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/editorials', payload)
    return data
  },

  /**
   * Actualiza una editorial existente.
   * @param {number} id
   * @param {{ name: string, country: string }} payload
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/editorials/${id}`, payload)
    return data
  },

  /**
   * Elimina una editorial por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/editorials/${id}`)
  },
}
