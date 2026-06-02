<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { tomesService } from '@/api/services/tomesService'
import { editionsService } from '@/api/services/editionsService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const tomos = ref([])
const ediciones = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const tomoEditando = ref(null)
const tomoAEliminar = ref(null)

const form = reactive({
  numberTome: '',
  title: '',
  editionId: '',
})

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
    const [tomosData, edicionesData] = await Promise.all([
      tomesService.listar(),
      editionsService.listar(),
    ])
    tomos.value = tomosData
    ediciones.value = edicionesData
  } catch {
    toast.error('No se pudieron cargar los tomos.')
  } finally {
    cargando.value = false
  }
}

function etiquetaEdicion(edicion) {
  return edicion.title ? `${edicion.title} (#${edicion.id})` : `Edición #${edicion.id}`
}

function resetForm() {
  Object.assign(form, { numberTome: '', title: '', editionId: '' })
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  tomoEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(tomo) {
  tomoEditando.value = tomo
  Object.assign(form, {
    numberTome: tomo.numberTome ?? '',
    title: tomo.title ?? '',
    editionId: tomo.editionId ?? '',
  })
  bsModalForm.show()
}

function abrirModalEliminar(tomo) {
  tomoAEliminar.value = tomo
  bsModalEliminar.show()
}

// ─── Guardar ──────────────────────────────────────────────────────────────────
async function guardar() {
  if (!form.numberTome || Number(form.numberTome) < 1) {
    toast.warning('El número de tomo es obligatorio y debe ser mayor que 0.')
    return
  }
  if (!form.editionId) {
    toast.warning('Debes seleccionar una edición.')
    return
  }
  guardando.value = true
  try {
    const payload = {
      numberTome: Number(form.numberTome),
      title: form.title.trim() || null,
      editionId: Number(form.editionId),
    }

    if (tomoEditando.value) {
      const actualizado = await tomesService.actualizar(tomoEditando.value.id, payload)
      const idx = tomos.value.findIndex((t) => t.id === tomoEditando.value.id)
      if (idx !== -1) tomos.value[idx] = actualizado
      toast.success(`Tomo ${payload.numberTome} actualizado correctamente.`)
    } else {
      const nuevo = await tomesService.crear(payload)
      tomos.value.unshift(nuevo)
      toast.success(`Tomo ${payload.numberTome} creado correctamente.`)
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
  if (!tomoAEliminar.value) return
  eliminando.value = true
  try {
    await tomesService.eliminar(tomoAEliminar.value.id)
    tomos.value = tomos.value.filter((t) => t.id !== tomoAEliminar.value.id)
    toast.success(`Tomo ${tomoAEliminar.value.numberTome} eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    tomoAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Tomos</h1>
        <p class="admin-page-subtitle">Gestión de tomos del catálogo</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nuevo Tomo
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los tomos
          <span class="admin-table-count">({{ tomos.length }} total)</span>
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
              <th>Nº Tomo</th>
              <th>Título</th>
              <th>Edición</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="tomo in tomos" :key="tomo.id">
              <td class="admin-table-id">{{ tomo.id }}</td>
              <td>
                <span class="admin-badge">Tomo {{ tomo.numberTome }}</span>
              </td>
              <td>
                <span class="admin-table-name">{{ tomo.title ?? '—' }}</span>
              </td>
              <td class="admin-table-meta">{{ tomo.editionTitle ?? '—' }}</td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(tomo)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(tomo)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && tomos.length === 0">
              <td colspan="5" class="text-center py-4 admin-table-empty">
                No hay tomos registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Tomo
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ tomoEditando ? 'Editar Tomo' : 'Nuevo Tomo' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <!-- Número de tomo -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">
                  Nº Tomo <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.numberTome"
                  type="number"
                  min="1"
                  class="form-control form-control-sm"
                  placeholder="Ej: 1"
                />
              </div>
              <!-- Título (opcional) -->
              <div class="col-md-8">
                <label class="form-label fw-semibold">Título</label>
                <input
                  v-model="form.title"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: El despertar del Hokage"
                />
              </div>
              <!-- Edición -->
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Edición <span class="text-danger">*</span>
                </label>
                <select v-model="form.editionId" class="form-select form-select-sm">
                  <option value="">— Selecciona una edición —</option>
                  <option v-for="ed in ediciones" :key="ed.id" :value="ed.id">
                    {{ etiquetaEdicion(ed) }}
                  </option>
                </select>
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
              {{ tomoEditando ? 'Guardar Cambios' : 'Crear Tomo' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Tomo
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Tomo</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar el
              <strong>Tomo {{ tomoAEliminar?.numberTome }}</strong>?
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
