<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { usersService } from '@/api/services/usersService'
import { uploadService } from '@/api/services/uploadService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const usuarios = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const usuarioEditando = ref(null)
const usuarioAEliminar = ref(null)

const ROLES = ['REGISTERED', 'SUBSCRIBED', 'ADMIN']

const form = reactive({
  username: '',
  email: '',
  name: '',
  secondName: '',
  img: '',
  role: '',
})
const imgFileSeleccionado = ref(null)

// ─── Referencias a modales Bootstrap ─────────────────────────────────────────
const refModalCrear = ref(null)
const refModalEditar = ref(null)
const refModalEliminar = ref(null)
let bsModalCrear, bsModalEditar, bsModalEliminar

const formCrear = reactive({
  username: '',
  password: '',
  email: '',
  name: '',
  secondName: '',
  role: 'REGISTERED',
})
const creando = ref(false)

onMounted(async () => {
  bsModalCrear = new Modal(refModalCrear.value)
  bsModalEditar = new Modal(refModalEditar.value)
  bsModalEliminar = new Modal(refModalEliminar.value)
  await cargarDatos()
})

async function cargarDatos() {
  cargando.value = true
  try {
    usuarios.value = await usersService.listar()
  } catch {
    toast.error('No se pudieron cargar los usuarios.')
  } finally {
    cargando.value = false
  }
}

function inicial(username) {
  return username ? username.charAt(0).toUpperCase() : 'U'
}

function nombreCompleto(user) {
  const parts = [user.name, user.secondName].filter(Boolean)
  return parts.length ? parts.join(' ') : '—'
}

function claseRol(role) {
  if (role === 'ADMIN') return 'admin-badge-admin'
  if (role === 'SUBSCRIBED') return 'admin-badge-sub'
  return ''
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  return d.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function resetFormCrear() {
  Object.assign(formCrear, { username: '', password: '', email: '', name: '', secondName: '', role: 'REGISTERED' })
}

async function crearUsuario() {
  if (!formCrear.username.trim() || !formCrear.password.trim() || !formCrear.email.trim()) {
    toast.warning('Usuario, contraseña y email son obligatorios.')
    return
  }
  creando.value = true
  try {
    const nuevo = await usersService.crear({
      username: formCrear.username.trim(),
      password: formCrear.password.trim(),
      email: formCrear.email.trim(),
      name: formCrear.name.trim() || null,
      secondName: formCrear.secondName.trim() || null,
      role: formCrear.role || 'REGISTERED',
    })
    usuarios.value.unshift(nuevo)
    toast.success(`Usuario "${nuevo.username}" creado correctamente.`)
    bsModalCrear.hide()
    resetFormCrear()
  } catch {
    // El interceptor ya muestra el toast de error
  } finally {
    creando.value = false
  }
}

function resetForm() {
  Object.assign(form, { username: '', email: '', name: '', secondName: '', img: '', role: '' })
  imgFileSeleccionado.value = null
  const inp = document.getElementById('uImgFile')
  if (inp) inp.value = ''
}

function abrirModalEditar(usuario) {
  usuarioEditando.value = usuario
  Object.assign(form, {
    username: usuario.username ?? '',
    email: usuario.email ?? '',
    name: usuario.name ?? '',
    secondName: usuario.secondName ?? '',
    img: usuario.img ?? '',
    role: usuario.role ?? '',
  })
  imgFileSeleccionado.value = null
  bsModalEditar.show()
}

function abrirModalEliminar(usuario) {
  usuarioAEliminar.value = usuario
  bsModalEliminar.show()
}

async function guardar() {
  if (!form.username.trim() || !form.email.trim()) {
    toast.warning('El nombre de usuario y el email son obligatorios.')
    return
  }
  guardando.value = true
  try {
    if (imgFileSeleccionado.value) {
      form.img = await uploadService.subir(imgFileSeleccionado.value)
    }
    const payload = {
      username: form.username.trim(),
      email: form.email.trim(),
      name: form.name.trim() || null,
      secondName: form.secondName.trim() || null,
      img: form.img || null,
      role: form.role || null,
    }
    const actualizado = await usersService.actualizar(usuarioEditando.value.id, payload)
    const idx = usuarios.value.findIndex((u) => u.id === usuarioEditando.value.id)
    if (idx !== -1) usuarios.value[idx] = actualizado
    toast.success(`Usuario "${payload.username}" actualizado correctamente.`)
    bsModalEditar.hide()
    resetForm()
  } catch {
    // El interceptor ya muestra el toast de error
  } finally {
    guardando.value = false
  }
}

async function confirmarEliminar() {
  if (!usuarioAEliminar.value) return
  eliminando.value = true
  try {
    await usersService.eliminar(usuarioAEliminar.value.id)
    usuarios.value = usuarios.value.filter((u) => u.id !== usuarioAEliminar.value.id)
    toast.success(`Usuario "${usuarioAEliminar.value.username}" eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    usuarioAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Comunidad</h1>
        <p class="admin-page-subtitle">Gestión de usuarios registrados</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="bsModalCrear.show()">
        <span class="material-symbols-outlined admin-topbar-btn-icon">person_add</span>
        Nuevo Usuario
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los usuarios
          <span class="admin-table-count">({{ usuarios.length }} total)</span>
        </p>
      </div>

      <div v-if="cargando" class="text-center py-4">
        <div class="spinner-border text-secondary" role="status"></div>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Avatar</th>
              <th>Usuario</th>
              <th>Email</th>
              <th>Nombre</th>
              <th>Rol</th>
              <th>Registro</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="usuario in usuarios" :key="usuario.id">
              <td>
                <img
                  v-if="usuario.img"
                  :src="usuario.img"
                  alt="avatar"
                  class="admin-author-photo"
                />
                <div v-else class="admin-user-avatar admin-author-avatar-sm">
                  {{ inicial(usuario.username) }}
                </div>
              </td>
              <td>
                <span class="admin-table-name">{{ usuario.username }}</span>
              </td>
              <td class="admin-table-meta">{{ usuario.email }}</td>
              <td class="admin-table-meta">{{ nombreCompleto(usuario) }}</td>
              <td>
                <span class="admin-badge" :class="claseRol(usuario.role)">
                  {{ usuario.role }}
                </span>
              </td>
              <td class="admin-table-meta">{{ formatFecha(usuario.registrationDate) }}</td>
              <td>
                <span v-if="usuario.active" class="admin-user-status-active">Activo</span>
                <span v-else class="admin-user-status-inactive">Inactivo</span>
              </td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(usuario)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(usuario)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && usuarios.length === 0">
              <td colspan="8" class="text-center py-4 admin-table-empty">
                No hay usuarios registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear Usuario
         ===================================================================== -->
    <div ref="refModalCrear" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Nuevo Usuario</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Nombre de usuario <span class="text-danger">*</span>
                </label>
                <input v-model="formCrear.username" type="text" class="form-control form-control-sm" placeholder="ej: juan123" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Contraseña <span class="text-danger">*</span>
                </label>
                <input v-model="formCrear.password" type="password" class="form-control form-control-sm" placeholder="Mínimo 6 caracteres" />
              </div>
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Email <span class="text-danger">*</span>
                </label>
                <input v-model="formCrear.email" type="email" class="form-control form-control-sm" placeholder="usuario@email.com" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Nombre</label>
                <input v-model="formCrear.name" type="text" class="form-control form-control-sm" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Apellido</label>
                <input v-model="formCrear.secondName" type="text" class="form-control form-control-sm" />
              </div>
              <div class="col-12">
                <label class="form-label fw-semibold">Rol</label>
                <select v-model="formCrear.role" class="form-select form-select-sm">
                  <option v-for="r in ROLES" :key="r" :value="r">{{ r }}</option>
                </select>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-sm btn-primary" :disabled="creando" @click="crearUsuario">
              <span v-if="creando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              Crear Usuario
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Editar Usuario
         ===================================================================== -->
    <div ref="refModalEditar" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Editar Usuario</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label fw-semibold">Nombre de usuario</label>
                <input v-model="form.username" type="text" class="form-control form-control-sm" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Email</label>
                <input v-model="form.email" type="email" class="form-control form-control-sm" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Nombre</label>
                <input v-model="form.name" type="text" class="form-control form-control-sm" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Apellido</label>
                <input v-model="form.secondName" type="text" class="form-control form-control-sm" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Rol</label>
                <select v-model="form.role" class="form-select form-select-sm">
                  <option value="">— Sin cambio —</option>
                  <option v-for="r in ROLES" :key="r" :value="r">{{ r }}</option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Foto de perfil</label>
                <input
                  id="uImgFile"
                  type="file"
                  accept="image/*"
                  class="form-control form-control-sm"
                  @change="imgFileSeleccionado = $event.target.files[0] || null"
                />
                <div v-if="usuarioEditando && form.img" class="mt-1 admin-img-preview">
                  Actual: {{ form.img }}
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-sm btn-primary"
              :disabled="guardando"
              @click="guardar"
            >
              <span v-if="guardando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              Guardar Cambios
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Usuario
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Usuario</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar al usuario
              <strong>"{{ usuarioAEliminar?.username }}"</strong>?
              Esta acción no se puede deshacer.
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-sm btn-danger"
              :disabled="eliminando"
              @click="confirmarEliminar"
            >
              <span v-if="eliminando" class="spinner-border spinner-border-sm me-1" role="status"></span>
              Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
