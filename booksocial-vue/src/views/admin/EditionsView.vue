<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { editionsService } from '@/api/services/editionsService'
import { worksService } from '@/api/services/worksService'
import { editorialsService } from '@/api/services/editorialsService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const ediciones = ref([])
const obras = ref([])
const editoriales = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const edicionEditando = ref(null)
const edicionAEliminar = ref(null)

const form = reactive({
  title: '',
  totalTomes: '',
  isbn: '',
  editionDate: '',
  workId: '',
  editorialId: '',
  price: '',
  stock: '',
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
    const [edicionesData, obrasData, editorialesData] = await Promise.all([
      editionsService.listar(),
      worksService.listar(),
      editorialsService.listar(),
    ])
    ediciones.value = edicionesData
    obras.value = obrasData
    editoriales.value = editorialesData
  } catch {
    toast.error('No se pudieron cargar las ediciones.')
  } finally {
    cargando.value = false
  }
}

function resetForm() {
  Object.assign(form, {
    title: '',
    totalTomes: '',
    isbn: '',
    editionDate: '',
    workId: '',
    editorialId: '',
    price: '',
    stock: '',
  })
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  edicionEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(edicion) {
  edicionEditando.value = edicion
  Object.assign(form, {
    title: edicion.title ?? '',
    totalTomes: edicion.totalTomes ?? '',
    isbn: edicion.isbn ?? '',
    editionDate: edicion.editionDate ?? '',
    workId: edicion.workId ?? '',
    editorialId: edicion.editorialId ?? '',
    price: '',
    stock: '',
  })
  bsModalForm.show()
}

function abrirModalEliminar(edicion) {
  edicionAEliminar.value = edicion
  bsModalEliminar.show()
}

// ─── Guardar ──────────────────────────────────────────────────────────────────
async function guardar() {
  if (!form.title.trim()) {
    toast.warning('El título es obligatorio.')
    return
  }
  if (!form.workId) {
    toast.warning('Debes seleccionar una obra.')
    return
  }
  if (!form.editorialId) {
    toast.warning('Debes seleccionar una editorial.')
    return
  }
  guardando.value = true
  try {
    const payload = {
      title: form.title.trim(),
      totalTomes: form.totalTomes !== '' ? Number(form.totalTomes) : null,
      isbn: form.isbn || null,
      editionDate: form.editionDate || null,
      workId: Number(form.workId),
      editorialId: Number(form.editorialId),
      price: form.price !== '' ? Number(form.price) : null,
      stock: form.stock !== '' ? Number(form.stock) : null,
    }

    if (edicionEditando.value) {
      const actualizada = await editionsService.actualizar(edicionEditando.value.id, payload)
      const idx = ediciones.value.findIndex((e) => e.id === edicionEditando.value.id)
      if (idx !== -1) ediciones.value[idx] = actualizada
      toast.success(`"${payload.title}" actualizada correctamente.`)
    } else {
      const nueva = await editionsService.crear(payload)
      ediciones.value.unshift(nueva)
      toast.success(`"${payload.title}" creada correctamente.`)
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
  if (!edicionAEliminar.value) return
  eliminando.value = true
  try {
    await editionsService.eliminar(edicionAEliminar.value.id)
    ediciones.value = ediciones.value.filter((e) => e.id !== edicionAEliminar.value.id)
    toast.success(`"${edicionAEliminar.value.title}" eliminada.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    edicionAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Ediciones</h1>
        <p class="admin-page-subtitle">Gestión de ediciones del catálogo</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nueva Edición
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todas las ediciones
          <span class="admin-table-count">({{ ediciones.length }} total)</span>
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
              <th>Título edición</th>
              <th>ISBN</th>
              <th>Obra</th>
              <th>Editorial</th>
              <th>Tomos</th>
              <th>Fecha</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="edicion in ediciones" :key="edicion.id">
              <td class="admin-table-id">{{ edicion.id }}</td>
              <td>
                <span class="admin-table-name">{{ edicion.title }}</span>
              </td>
              <td class="admin-table-meta">
                <span v-if="edicion.isbn" class="admin-inv-isbn">{{ edicion.isbn }}</span>
                <span v-else>—</span>
              </td>
              <td class="admin-table-meta">{{ edicion.workTitle ?? '—' }}</td>
              <td class="admin-table-meta">{{ edicion.editorialName ?? '—' }}</td>
              <td class="admin-table-meta">
                <span v-if="edicion.totalTomes" class="admin-badge">{{ edicion.totalTomes }}</span>
                <span v-else>—</span>
              </td>
              <td class="admin-table-meta">{{ edicion.editionDate ?? '—' }}</td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(edicion)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(edicion)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && ediciones.length === 0">
              <td colspan="8" class="text-center py-4 admin-table-empty">
                No hay ediciones registradas
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Edición
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ edicionEditando ? 'Editar Edición' : 'Nueva Edición' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <!-- Título -->
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Título <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.title"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: Naruto Edición Coleccionista"
                />
              </div>
              <!-- Obra -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Obra <span class="text-danger">*</span>
                </label>
                <select v-model="form.workId" class="form-select form-select-sm">
                  <option value="">— Selecciona una obra —</option>
                  <option v-for="obra in obras" :key="obra.id" :value="obra.id">
                    {{ obra.title }}
                  </option>
                </select>
              </div>
              <!-- Editorial -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">
                  Editorial <span class="text-danger">*</span>
                </label>
                <select v-model="form.editorialId" class="form-select form-select-sm">
                  <option value="">— Selecciona una editorial —</option>
                  <option v-for="ed in editoriales" :key="ed.id" :value="ed.id">
                    {{ ed.name }}
                  </option>
                </select>
              </div>
              <!-- Total tomos -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Nº total de tomos</label>
                <input
                  v-model="form.totalTomes"
                  type="number"
                  min="1"
                  class="form-control form-control-sm"
                  placeholder="Ej: 72 (manga/cómic)"
                />
              </div>
              <!-- ISBN -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">ISBN</label>
                <input
                  v-model="form.isbn"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: 978-84-679-6250-9"
                />
              </div>
              <!-- Fecha edición -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Fecha de edición</label>
                <input
                  v-model="form.editionDate"
                  type="date"
                  class="form-control form-control-sm"
                />
              </div>

              <!-- Separador producto -->
              <div class="col-12">
                <hr class="admin-modal-divider" />
                <p class="form-text mb-2">
                  Si indicas precio y stock se creará automáticamente un producto asociado a esta edición.
                </p>
              </div>
              <!-- Precio -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Precio (€)</label>
                <input
                  v-model="form.price"
                  type="number"
                  min="0"
                  step="0.01"
                  class="form-control form-control-sm"
                  placeholder="Ej: 12.99"
                />
              </div>
              <!-- Stock -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Stock inicial</label>
                <input
                  v-model="form.stock"
                  type="number"
                  min="0"
                  class="form-control form-control-sm"
                  placeholder="Ej: 50"
                />
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
              {{ edicionEditando ? 'Guardar Cambios' : 'Crear Edición' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Edición
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Edición</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar la edición
              <strong>"{{ edicionAEliminar?.title }}"</strong>?
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
