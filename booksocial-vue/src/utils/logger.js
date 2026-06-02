/**
 * Centraliza los mensajes de consola de la aplicación
 */

const isDev = import.meta.env.DEV

// Estilos de consola para cada nivel
const STYLES = {
  debug: 'color: #6b7280; font-weight: 600',
  info: 'color: #2D4C3E; font-weight: 600',
  warn: 'color: #d97706; font-weight: 600',
  error: 'color: #dc2626; font-weight: 700',
}

/**
 * Formatea y emite un mensaje en consola.
 * @param {'debug'|'info'|'warn'|'error'} level
 * @param {string} message
 * @param {any} [extra] - datos adicionales (objeto, error, etc.)
 */
function log(level, message, extra) {
  const prefix = `[BookSocial Admin] [${level.toUpperCase()}]`

  if (level === 'error') {
    console.error(`%c${prefix} ${message}`, STYLES[level], extra ?? '')
  } else if (level === 'warn') {
    console.warn(`%c${prefix} ${message}`, STYLES[level], extra ?? '')
  } else if (isDev) {
    // debug e info solo en desarrollo
    console.log(`%c${prefix} ${message}`, STYLES[level], extra ?? '')
  }
}

// Con el esto se exporta el objeto logger
export const logger = {
  /** @param {string} msg @param {any} [extra] */
  debug: (msg, extra) => log('debug', msg, extra),
  /** @param {string} msg @param {any} [extra] */
  info: (msg, extra) => log('info', msg, extra),
  /** @param {string} msg @param {any} [extra] */
  warn: (msg, extra) => log('warn', msg, extra),
  /** @param {string} msg @param {any} [extra] */
  error: (msg, extra) => log('error', msg, extra),
}
