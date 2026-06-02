/**
 * api/services/authService.js — Servicio de autenticación.
 *
 * Contrato del backend:
 *   POST /api/auth/login → { token, type, userId, username, role }
 */

import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

export const authService = {
  /**
   * Inicia sesión en el backend y devuelve los datos del usuario + token JWT.
   * @param {{ username: string, password: string }} credenciales
   * @returns {Promise<{ token: string, type: string, userId: number, username: string, role: string }>}
   */
  async login(credenciales) {
    const { data } = await axios.post(`${BASE_URL}/auth/login`, credenciales)
    return data
  },
}
