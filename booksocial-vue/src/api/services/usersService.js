/**
 * api/services/usersService.js — Servicio CRUD de Usuarios.
 *
 * Consume los endpoints del backend:
 *   GET    /api/users       → lista todos los usuarios
 *   POST   /api/users       → crea un nuevo usuario
 *   PUT    /api/users/{id}  → actualiza un usuario
 *   DELETE /api/users/{id}  → elimina un usuario
 */

import apiClient from '@/api/axios'

export const usersService = {
  /**
   * Lista todos los usuarios.
   * @returns {Promise<Array>}
   */
  async listar() {
    const { data } = await apiClient.get('/users')
    return Array.isArray(data) ? data : data.content ?? []
  },

  /**
   * Crea un nuevo usuario.
   * @param {Object} payload Datos del usuario.
   * @returns {Promise<Object>}
   */
  async crear(payload) {
    const { data } = await apiClient.post('/users', payload)
    return data
  },

  /**
   * Actualiza un usuario existente.
   * @param {number} id
   * @param {Object} payload Datos actualizados del usuario.
   * @returns {Promise<Object>}
   */
  async actualizar(id, payload) {
    const { data } = await apiClient.put(`/users/${id}`, payload)
    return data
  },

  /**
   * Elimina un usuario por id.
   * @param {number} id
   * @returns {Promise<void>}
   */
  async eliminar(id) {
    await apiClient.delete(`/users/${id}`)
  },
}
