<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { volumesService } from '@/api/services/volumesService'
import { editionsService } from '@/api/services/editionsService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const volumenes = ref([])
const ediciones = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const volumenEditando = ref(null)
const volumenAEliminar = ref(null)

const form = reactive({
  volumeNumber: '',
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
    const [volumenesData, edicionesData] = await Promise.all([
      volumesService.listar(),
      editionsService.listar(),
    ])
    volumenes.value = volumenesData
    ediciones.value = edicionesData
  } catch {
    toast.error('No se pudieron cargar los volúmenes.')
  } finally {
    cargando.value = false
  }
}

function etiquetaEdicion(edicion) {
  return edicion.title ? `${edicion.title} (#${edicion.id})` : `Edición #${edicion.id}`
}

function resetForm() {
  Object.assign(form, { volumeNumber: '', title: '', editionId: '' })
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  volumenEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(volumen) {
  volumenEditando.value = volumen
  Object.assign(form, {
    volumeNumber: volumen.volumeNumber ?? '',
    title: volumen.title ?? '',
    editionId: volumen.editionId ?? '',
  })
  bsModalForm.show()
}

function abrirModalEliminar(volumen) {
  volumenAEliminar.value = volumen
  bsModalEliminar.show()
}

// ─── Guardar ──────────────────────────────────────────────────────────────────
async function guardar() {
  if (!form.volumeNumber || Number(form.volumeNumber) < 1) {
    toast.warning('El número de volumen es obligatorio y debe ser mayor que 0.')
    return
  }
  if (!form.editionId) {
    toast.warning('Debes seleccionar una edición.')
    return
  }
  guardando.value = true
  try {
    const payload = {
      volumeNumber: Number(form.volumeNumber),
      title: form.title.trim() || null,
      editionId: Number(form.editionId),
    }

    if (volumenEditando.value) {
      const actualizado = await volumesService.actualizar(volumenEditando.value.id, payload)
      const idx = volumenes.value.findIndex((v) => v.id === volumenEditando.value.id)
      if (idx !== -1) volumenes.value[idx] = actualizado
      toast.success(`Vol. ${payload.volumeNumber} actualizado correctamente.`)
    } else {
      const nuevo = await volumesService.crear(payload)
      volumenes.value.unshift(nuevo)
      toast.success(`Vol. ${payload.volumeNumber} creado correctamente.`)
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
  if (!volumenAEliminar.value) return
  eliminando.value = true
  try {
    await volumesService.eliminar(volumenAEliminar.value.id)
    volumenes.value = volumenes.value.filter((v) => v.id !== volumenAEliminar.value.id)
    toast.success(`Vol. ${volumenAEliminar.value.volumeNumber} eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    volumenAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Volúmenes</h1>
        <p class="admin-page-subtitle">Gestión de volúmenes de cómic</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nuevo Volumen
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los volúmenes
          <span class="admin-table-count">({{ volumenes.length }} total)</span>
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
              <th>Nº Vol.</th>
              <th>Título</th>
              <th>Edición</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="volumen in volumenes" :key="volumen.id">
              <td class="admin-table-id">{{ volumen.id }}</td>
              <td>
                <span class="admin-badge">Vol. {{ volumen.volumeNumber }}</span>
              </td>
              <td>
                <span class="admin-table-name">{{ volumen.title ?? '—' }}</span>
              </td>
              <td class="admin-table-meta">
                {{ volumen.editionTitle ?? (volumen.editionId ? `Edición #${volumen.editionId}` : '—') }}
              </td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(volumen)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(volumen)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && volumenes.length === 0">
              <td colspan="5" class="text-center py-4 admin-table-empty">
                No hay volúmenes registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Volumen
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ volumenEditando ? 'Editar Volumen' : 'Nuevo Volumen' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <!-- Número de volumen -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">
                  Nº Volumen <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.volumeNumber"
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
                  placeholder="Ej: El comienzo del fin"
                />
              </div>
              <!-- Edición -->
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Edición <span class="text-danger">*</span>
                </label>
                <select v-model="form.editionId" class="form-select form-select-sm">
                  <option value="">— Seleccionar edición —</option>
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
              {{ volumenEditando ? 'Guardar Cambios' : 'Crear Volumen' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Volumen
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Volumen</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar el
              <strong>Vol. {{ volumenAEliminar?.volumeNumber }}</strong>?
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
