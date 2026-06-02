<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { chaptersService } from '@/api/services/chaptersService'
import { tomesService } from '@/api/services/tomesService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const capitulos = ref([])
const tomos = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const capituloEditando = ref(null)
const capituloAEliminar = ref(null)

const form = reactive({
  chapterNumber: '',
  title: '',
  tomeId: '',
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
    const [capitulosData, tomosData] = await Promise.all([
      chaptersService.listar(),
      tomesService.listar(),
    ])
    capitulos.value = capitulosData
    tomos.value = tomosData
  } catch {
    toast.error('No se pudieron cargar los capítulos.')
  } finally {
    cargando.value = false
  }
}

function etiquetaTomo(tomo) {
  return tomo.title
    ? `T${tomo.numberTome} — ${tomo.title}`
    : `Tomo #${tomo.numberTome}`
}

function resetForm() {
  Object.assign(form, { chapterNumber: '', title: '', tomeId: '' })
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  capituloEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(capitulo) {
  capituloEditando.value = capitulo
  Object.assign(form, {
    chapterNumber: capitulo.chapterNumber ?? '',
    title: capitulo.title ?? '',
    tomeId: capitulo.tomeId ?? '',
  })
  bsModalForm.show()
}

function abrirModalEliminar(capitulo) {
  capituloAEliminar.value = capitulo
  bsModalEliminar.show()
}

// ─── Guardar ──────────────────────────────────────────────────────────────────
async function guardar() {
  if (!form.chapterNumber || Number(form.chapterNumber) < 1) {
    toast.warning('El número de capítulo es obligatorio y debe ser mayor que 0.')
    return
  }
  if (!form.tomeId) {
    toast.warning('Debes seleccionar un tomo.')
    return
  }
  guardando.value = true
  try {
    const payload = {
      chapterNumber: Number(form.chapterNumber),
      title: form.title.trim() || null,
      tomeId: Number(form.tomeId),
    }

    if (capituloEditando.value) {
      const actualizado = await chaptersService.actualizar(capituloEditando.value.id, payload)
      const idx = capitulos.value.findIndex((c) => c.id === capituloEditando.value.id)
      if (idx !== -1) capitulos.value[idx] = actualizado
      toast.success(`Cap. ${payload.chapterNumber} actualizado correctamente.`)
    } else {
      const nuevo = await chaptersService.crear(payload)
      capitulos.value.unshift(nuevo)
      toast.success(`Cap. ${payload.chapterNumber} creado correctamente.`)
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
  if (!capituloAEliminar.value) return
  eliminando.value = true
  try {
    await chaptersService.eliminar(capituloAEliminar.value.id)
    capitulos.value = capitulos.value.filter((c) => c.id !== capituloAEliminar.value.id)
    toast.success(`Cap. ${capituloAEliminar.value.chapterNumber} eliminado.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    capituloAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Capítulos</h1>
        <p class="admin-page-subtitle">Gestión de capítulos del catálogo</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nuevo Capítulo
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todos los capítulos
          <span class="admin-table-count">({{ capitulos.length }} total)</span>
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
              <th>Nº Cap.</th>
              <th>Título</th>
              <th>Tomo</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="capitulo in capitulos" :key="capitulo.id">
              <td class="admin-table-id">{{ capitulo.id }}</td>
              <td>
                <span class="admin-badge">Cap. {{ capitulo.chapterNumber }}</span>
              </td>
              <td>
                <span class="admin-table-name">{{ capitulo.title ?? '—' }}</span>
              </td>
              <td class="admin-table-meta">{{ capitulo.tomeTitle ?? '—' }}</td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(capitulo)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(capitulo)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && capitulos.length === 0">
              <td colspan="5" class="text-center py-4 admin-table-empty">
                No hay capítulos registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Capítulo
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ capituloEditando ? 'Editar Capítulo' : 'Nuevo Capítulo' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <!-- Número de capítulo -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">
                  Nº Capítulo <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.chapterNumber"
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
                  placeholder="Ej: El primer raikage"
                />
              </div>
              <!-- Tomo -->
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Tomo <span class="text-danger">*</span>
                </label>
                <select v-model="form.tomeId" class="form-select form-select-sm">
                  <option value="">— Selecciona un tomo —</option>
                  <option v-for="tomo in tomos" :key="tomo.id" :value="tomo.id">
                    {{ etiquetaTomo(tomo) }}
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
              {{ capituloEditando ? 'Guardar Cambios' : 'Crear Capítulo' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Capítulo
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Capítulo</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar el
              <strong>Cap. {{ capituloAEliminar?.chapterNumber }}</strong>?
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
