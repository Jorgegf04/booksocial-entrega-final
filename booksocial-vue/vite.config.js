// vite.config.js — Configuración del bundler Vite para el panel admin Vue
// Registra el plugin oficial de Vue y configura el alias @ → src/

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    // @ es un alias a la carpeta src/ (igual que en proyectos CLI de Vue)
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
  },
})
