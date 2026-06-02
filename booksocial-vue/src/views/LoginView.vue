<!--
  LoginView.vue — Vista de inicio de sesión del panel admin.

  Replica el diseño del login Thymeleaf (auth/login.html):
    - Panel izquierdo decorativo con cita literaria
    - Panel derecho con formulario

  Validación:
    VeeValidate + Yup: username requerido, password mínimo 6 caracteres.

  Flujo:
    1. Usuario envía el formulario.
    2. Se llama a authStore.login({ username, password }).
    3. authService.login() llama a POST /api/auth/login.
    4. Si OK → router redirige a /admin/dashboard.
    5. Si error → se muestra el mensaje en un toast + alerta inline.

  Store: useAuthStore (login, isAuthenticated)
  CSS: auth.css (variables + clases .auth-*) 

-->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useForm, useField } from 'vee-validate'
import * as yup from 'yup'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

// Estado local: mensaje de error inline (además del toast)
const errorLogin = ref(null)
const cargando = ref(false)
// Alterna visibilidad de la contraseña
const mostrarPassword = ref(false)

// Codigo de la ilustración 54
//  Esquema de validación con Yup
const esquema = yup.object({
  username: yup.string().required('El nombre de usuario es obligatorio.'),
  password: yup
    .string()
    .required('La contraseña es obligatoria.')
    .min(6, 'La contraseña debe tener al menos 6 caracteres.'),
})

//  VeeValidate: useForm + useField 
const { handleSubmit } = useForm({ validationSchema: esquema })
const { value: username, errorMessage: usernameError } = useField('username')
const { value: password, errorMessage: passwordError } = useField('password')

// Submit 
const onSubmit = handleSubmit(async (values) => {
  cargando.value = true
  errorLogin.value = null
  try {
    await authStore.login({ username: values.username, password: values.password })
    // Login correcto → redirigir al dashboard
    router.push({ name: 'Dashboard' })
  } catch (err) {
    // El interceptor de axios ya muestra un toast; aquí mostramos el mensaje inline
    const msg =
      err.response?.data?.message ||
      err.response?.data?.error ||
      (err.response?.status === 401
        ? 'Credenciales incorrectas. Verifica tu usuario y contraseña.'
        : null) ||
      'Credenciales incorrectas. Inténtalo de nuevo.'
    errorLogin.value = msg
    toast.error(msg)
  } finally {
    cargando.value = false
  }
})
</script>

<template>
  <div class="auth-wrapper">

    <!-- Panel izquierdo decorativo (visible en ≥ 768 px) -->
    <div class="auth-panel">
      <div class="auth-brand"><span>Book</span>Social</div>

      <div class="auth-panel-middle">
        <p class="auth-panel-quote">
          "Un libro es un sueño que tienes en tus manos."
        </p>
        <p class="auth-panel-sub">Neil Gaiman</p>
      </div>

      <p class="auth-panel-footer">
        © 2026 BookSocial — El Curador Digital
      </p>
    </div>

    <!-- Panel derecho: formulario de login -->
    <div class="auth-form-side">
      <div class="auth-card">

        <h1 class="auth-card-title">Panel de administración</h1>
        <p class="auth-card-sub">Inicia sesión con tu cuenta de administrador</p>

        <!-- Alerta de error de credenciales -->
        <div v-if="errorLogin" class="alert alert-danger py-2 mb-3 auth-alert-text" role="alert">
          {{ errorLogin }}
        </div>

        <!-- Formulario con VeeValidate -->
        <form novalidate @submit.prevent="onSubmit">

          <!-- Campo: usuario -->
          <div class="mb-3">
            <label for="username" class="auth-label">Nombre de usuario</label>
            <input
              id="username"
              v-model="username"
              type="text"
              class="form-control auth-input"
              :class="{ 'is-invalid': usernameError }"
              placeholder="tu_usuario"
              autocomplete="username"
            />
            <div v-if="usernameError" class="invalid-feedback">{{ usernameError }}</div>
          </div>

          <!-- Campo: contraseña -->
          <div class="mb-4">
            <label for="loginPassword" class="auth-label">Contraseña</label>
            <div class="auth-input-group mt-1">
              <input
                id="loginPassword"
                v-model="password"
                :type="mostrarPassword ? 'text' : 'password'"
                class="form-control auth-input"
                :class="{ 'is-invalid': passwordError }"
                placeholder="••••••••"
                autocomplete="current-password"
              />
              <!-- Botón para alternar visibilidad de contraseña -->
              <button
                type="button"
                class="auth-pw-toggle"
                :title="mostrarPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
                @click="mostrarPassword = !mostrarPassword"
              >
                <!-- Ojo: abierto cuando se muestra, cerrado cuando está oculta -->
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.8"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <template v-if="mostrarPassword">
                    <!-- Ojo tachado (contraseña visible) -->
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                    <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                    <line x1="1" y1="1" x2="23" y2="23"/>
                  </template>
                  <template v-else>
                    <!-- Ojo abierto (contraseña oculta) -->
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </template>
                </svg>
              </button>
              <div v-if="passwordError" class="invalid-feedback d-block">{{ passwordError }}</div>
            </div>
          </div>

          <!-- Botón de envío -->
          <button type="submit" class="auth-btn" :disabled="cargando">
            <span v-if="cargando" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ cargando ? 'Iniciando sesión…' : 'Iniciar sesión' }}
          </button>
        </form>

      </div>
    </div>

  </div>
</template>

<style scoped>
/* Estilos de la vista de login Vue. */

/* Estilos de contenedor del login. */
.auth-wrapper {
  display: flex;
  min-height: 100vh;
}

/* Estilos de panel lateral del login. */
.auth-panel {
  flex: 0 0 42%;
  background-color: #163528;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 3rem;
}

/* Estilos de marca del login. */
.auth-brand {
  font-family: 'Noto Serif', serif;
  font-style: italic;
  font-size: 1.75rem;
  color: #c0ecda;
}

/* Estilos de panel lateral del login. */
.auth-panel-middle {
  text-align: center;
}

/* Estilos de panel lateral del login. */
.auth-panel-quote {
  font-family: 'Noto Serif', serif;
  font-style: italic;
  font-size: 1.5rem;
  color: #fff;
  line-height: 1.5;
  margin-bottom: 1rem;
}

/* Estilos de panel lateral del login. */
.auth-panel-sub {
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(192, 236, 218, 0.7);
}

/* Estilos de panel lateral del login. */
.auth-panel-footer {
  font-size: 0.78rem;
  color: rgba(255, 255, 255, 0.4);
  margin: 0;
}

/* Estilos de zona del formulario de login. */
.auth-form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0ede7;
  padding: 2rem;
}

/* Estilos de tarjeta del login. */
.auth-card {
  width: 100%;
  max-width: 420px;
}

/* Estilos de tarjeta del login. */
.auth-card-title {
  font-family: 'Noto Serif', serif;
  font-size: 1.6rem;
  font-weight: 700;
  color: #2D4C3E;
  margin-bottom: 0.35rem;
}

/* Estilos de tarjeta del login. */
.auth-card-sub {
  font-size: 0.875rem;
  color: #727974;
  margin-bottom: 1.75rem;
}

/* Estilos de etiquetas del formulario. */
.auth-label {
  display: block;
  font-size: 0.72rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #414844;
  margin-bottom: 0.4rem;
}

/* Estilos de campos del formulario. */
.auth-input {
  border: 1px solid #c1c8c2;
  border-radius: 0.5rem;
  padding: 0.65rem 1rem;
  font-size: 0.9rem;
  color: #1c1c18;
  background-color: #fff;
  transition: border-color 0.2s, box-shadow 0.2s;
  width: 100%;
}
/* Estilos de campos del formulario. */
.auth-input:focus {
  border-color: #2D4C3E;
  box-shadow: 0 0 0 0.2rem rgba(45, 76, 62, 0.12);
  outline: none;
}

/* Estilos de campos del formulario. */
.auth-input-group {
  position: relative;
}
/* Estilos de campos del formulario. */
.auth-input-group .auth-input {
  padding-right: 2.8rem;
}
/* Estilos de boton de contrasena. */
.auth-pw-toggle {
  position: absolute;
  top: 50%;
  right: 0.85rem;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 0;
  color: #727974;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.15s;
}
/* Estilos de boton de contrasena. */
.auth-pw-toggle:hover { color: #2D4C3E; }

/* Estilos de boton de login. */
.auth-btn {
  width: 100%;
  background-color: #2D4C3E;
  color: #fff;
  border: none;
  border-radius: 0.5rem;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.2s;
}
/* Estilos de boton de login. */
.auth-btn:hover:not(:disabled) { background-color: #1e3a2f; }
/* Estilos de boton de login. */
.auth-btn:disabled { opacity: 0.65; cursor: not-allowed; }

/* Estilos de mensaje del login. */
.auth-alert-text { font-size: 0.85rem; }

@media (max-width: 767px) {
  
/* Estilos de panel lateral del login. */
.auth-panel { display: none; }
}
</style>
