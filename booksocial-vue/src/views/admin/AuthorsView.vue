<!-- 
 REF-07.BOOKSOCIAL-VUE/VIEWS/ADMIN/WORKVIEW.VUE
-->
<!--
  AuthorsView.vue — Gestión de Autores.

  Qué hace esta vista:
    - Lista todos los autores con foto (o avatar de inicial), nombre,
      nacionalidad, fecha de nacimiento y nº de obras.
    - Permite crear, editar y eliminar autores.
    - Si se selecciona foto nueva, la sube a POST /api/upload antes de guardar.
    - El selector de obras usa los IDs (workIds) para asociar obras al autor.

  Servicios: authorsService, worksService (selector de obras), uploadService
-->
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { authorsService } from '@/api/services/authorsService'
import { worksService } from '@/api/services/worksService'
import { uploadService } from '@/api/services/uploadService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const autores = ref([])
const obras = ref([]) // para el selector de obras en el formulario
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const autorEditando = ref(null)
const autorAEliminar = ref(null)

const form = reactive({
  name: '',
  nationality: '',
  birthDate: '',
  img: '',
  workIds: [], // IDs de las obras asociadas
})
const imgFileSeleccionado = ref(null)

// ─── Referencias a modales Bootstrap ─────────────────────────────────────────
const refModalForm = ref(null)
const refModalEliminar = ref(null)
let bsModalForm, bsModalEliminar

onMounted(async () => {
  bsModalForm = new Modal(refModalForm.value)
  bsModalEliminar = new Modal(refModalEliminar.value)
  await cargarDatos()
})

async function cargarDatos() {
  cargando.value = true
  try {
    const [autoresData, obrasData] = await Promise.all([
      authorsService.listar(),
      worksService.listar(),
    ])
    autores.value = autoresData
    obras.value = obrasData
  } catch {
    toast.error('No se pudieron cargar los autores.')
  } finally {
    cargando.value = false
  }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
/** Inicial del nombre en mayúscula para el avatar */
function inicial(nombre) {
  return nombre ? nombre.charAt(0).toUpperCase() : 'A'
}

/** Resetea el formulario */
function resetForm() {
  Object.assign(form, { name: '', nationality: '', birthDate: '', img: '', workIds: [] })
  imgFileSeleccionado.value = null
  const inp = document.getElementById('aImgFile')
  if (inp) inp.value = ''
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  autorEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(autor) {
  autorEditando.value = autor
  // Extraemos los IDs de las obras del autor (works es List<WorkResponseDTO>)
  const workIds = autor.works?.map((w) => w.id) ?? []
  Object.assign(form, {
    name: autor.name ?? '',
    nationality: autor.nationality ?? '',
    birthDate: autor.birthDate ?? '',
    img: autor.img ?? '',
    workIds,
  })
  imgFileSeleccionado.value = null
  bsModalForm.show()
}

function abrirModalEliminar(autor) {
  autorAEliminar.value = autor
  bsModalEliminar.show()
}

// ─── Guardar ──────────────────────────────────────────────────────────────────
async function guardar() {
  if (!form.name.trim()) {
    toast.warning('El nombre es obligatorio.')
    return
  }
  guardando.value = true
  try {
    if (imgFileSeleccionado.value) {
      form.img = await uploadService.subir(imgFileSeleccionado.value)
    }
    const payload = {
      name: form.name.trim(),
      nationality: form.nationality || null,
      birthDate: form.birthDate || null,
      img: form.img || null,
      workIds: form.workIds,
    }

    if (autorEditando.value) {
      const actualizado = await authorsService.actualizar(autorEditando.value.id, payload)
      const idx = autores.value.findIndex((a) => a.id === autorEditando.value.id)
      if (idx !== -1) autores.value[idx] = actualizado
      toast.success(`"${payload.name}" actualizado correctamente.`)
    } else {
      const nuevo = await authorsService.crear(payload)
      autores.value.unshift(nuevo)
      toast.success(`"${payload.name}" creado correctamente.`)
    }
    bsModalForm.hide()
    resetForm()
  } catch {
    // El interceptor ya muestra el toast de error
  } finally {
    guardando.value = false
  }
}

// ─── Eliminar ─────────────────────────────────────────────────────────────────
async function confirmarEliminar() {
  if (!autorAEliminar.value) return
  eliminando.value = true
  try {
    await authorsService.eliminar(autorAEliminar.value.id)
    autores.value = autores.value.filter((a) => a.id !== autorAEliminar.value.id)
    toast.success(`"${autorAEliminar.value.name}" eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    autorAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Autores</h1>
        <p class="admin-page-subtitle">Gestión de autores del catálogo</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nuevo Autor
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los autores
          <span class="admin-table-count">({{ autores.length }} total)</span>
        </p>
      </div>

      <div v-if="cargando" class="text-center py-4">
        <div class="spinner-border text-secondary" role="status"></div>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Foto</th>
              <th>Nombre</th>
              <th>Nacionalidad</th>
              <th>Fecha nacimiento</th>
              <th>Nº obras</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="autor in autores" :key="autor.id">
              <td class="admin-table-id">{{ autor.id }}</td>
              <!-- Foto o avatar de iniciales -->
              <td>
                <img
                  v-if="autor.img"
                  :src="autor.img"
                  alt="foto"
                  class="admin-author-photo"
                />
                <div
                  v-else
                  class="admin-user-avatar admin-author-avatar-sm"
                >
                  {{ inicial(autor.name) }}
                </div>
              </td>
              <td>
                <span class="admin-table-name">{{ autor.name }}</span>
              </td>
              <td class="admin-table-meta">{{ autor.nationality ?? '—' }}</td>
              <td class="admin-table-meta">{{ autor.birthDate ?? '—' }}</td>
              <td>
                <span class="admin-badge">
                  {{ autor.works?.length ?? 0 }} obras
                </span>
              </td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(autor)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(autor)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && autores.length === 0">
              <td colspan="7" class="text-center py-4 admin-table-empty">
                No hay autores registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Autor
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ autorEditando ? 'Editar Autor' : 'Nuevo Autor' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <!-- Nombre -->
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Nombre <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: Masashi Kishimoto"
                />
              </div>
              <!-- Nacionalidad -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Nacionalidad</label>
                <input
                  v-model="form.nationality"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: Japonesa"
                />
              </div>
              <!-- Fecha de nacimiento -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Fecha de nacimiento</label>
                <input
                  v-model="form.birthDate"
                  type="date"
                  class="form-control form-control-sm"
                />
              </div>
              <!-- Foto -->
              <div class="col-12">
                <label class="form-label fw-semibold">Foto del autor</label>
                <input
                  id="aImgFile"
                  type="file"
                  accept="image/*"
                  class="form-control form-control-sm"
                  @change="imgFileSeleccionado = $event.target.files[0] || null"
                />
                <div v-if="autorEditando && form.img" class="mt-1 admin-img-preview">
                  Actual: {{ form.img }}
                </div>
              </div>
              <!-- Obras (multiselect por ID) -->
              <div class="col-12">
                <label class="form-label fw-semibold">Obras</label>
                <select
                  v-model="form.workIds"
                  class="form-select form-select-sm"
                  multiple
                  size="4"
                >
                  <option v-for="obra in obras" :key="obra.id" :value="obra.id">
                    {{ obra.title }}
                  </option>
                </select>
                <div class="form-text">Mantén Ctrl/⌘ para seleccionar varias obras</div>
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
              {{ autorEditando ? 'Guardar Cambios' : 'Crear Autor' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Autor
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Autor</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar a
              <strong>"{{ autorAEliminar?.name }}"</strong>?
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
