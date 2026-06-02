/**
 * api/services/uploadService.js — Servicio de subida de imágenes.
 *
 * El backend expone POST /api/upload con multipart/form-data.
 * Devuelve la URL (string) de la imagen almacenada.
 *
 * Usado por worksService y authorsService antes de crear/editar
 * una entidad que lleva imagen.
 */

import apiClient from '@/api/axios'

export const uploadService = {
  /**
   * Sube un archivo de imagen al servidor.
   * @param {File} file
   * @returns {Promise<string>}
   */
  async subir(file) {
    const formData = new FormData()
    formData.append('file', file)
    const { data } = await apiClient.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return data // el backend devuelve la URL como string
  },
}
