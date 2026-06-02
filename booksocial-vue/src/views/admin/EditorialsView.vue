<!--
  EditorialsView.vue — Gestión de Editoriales.

  La entidad más simple del panel: solo name y country, sin imágenes.
  Demuestra el patrón CRUD básico (create/edit compartido + modal eliminar)
  que se repite en las demás vistas.

  Servicio: editorialsService → GET/POST/PUT/DELETE /api/editorials
-->
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { editorialsService } from '@/api/services/editorialsService'

const toast = useToast()

// ─── Estado ───────────────────────────────────────────────────────────────────
const editoriales = ref([])
const cargando = ref(false)
const guardando = ref(false)
const eliminando = ref(false)

const editorialEditando = ref(null)
const editorialAEliminar = ref(null)

const form = reactive({ name: '', country: '' })

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
    editoriales.value = await editorialsService.listar()
  } catch {
    toast.error('No se pudieron cargar las editoriales.')
  } finally {
    cargando.value = false
  }
}

function resetForm() {
  Object.assign(form, { name: '', country: '' })
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  editorialEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(editorial) {
  editorialEditando.value = editorial
  Object.assign(form, { name: editorial.name ?? '', country: editorial.country ?? '' })
  bsModalForm.show()
}

function abrirModalEliminar(editorial) {
  editorialAEliminar.value = editorial
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
    const payload = {
      name: form.name.trim(),
      country: form.country || null,
    }

    if (editorialEditando.value) {
      const actualizada = await editorialsService.actualizar(editorialEditando.value.id, payload)
      const idx = editoriales.value.findIndex((e) => e.id === editorialEditando.value.id)
      if (idx !== -1) editoriales.value[idx] = actualizada
      toast.success(`"${payload.name}" actualizada correctamente.`)
    } else {
      const nueva = await editorialsService.crear(payload)
      editoriales.value.unshift(nueva)
      toast.success(`"${payload.name}" creada correctamente.`)
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
  if (!editorialAEliminar.value) return
  eliminando.value = true
  try {
    await editorialsService.eliminar(editorialAEliminar.value.id)
    editoriales.value = editoriales.value.filter((e) => e.id !== editorialAEliminar.value.id)
    toast.success(`"${editorialAEliminar.value.name}" eliminada.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    editorialAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Editoriales</h1>
        <p class="admin-page-subtitle">Gestión de editoriales del catálogo</p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nueva Editorial
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todas las editoriales
          <span class="admin-table-count">({{ editoriales.length }} total)</span>
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
              <th>Nombre</th>
              <th>País</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="editorial in editoriales" :key="editorial.id">
              <td class="admin-table-id">{{ editorial.id }}</td>
              <td>
                <span class="admin-table-name">{{ editorial.name }}</span>
              </td>
              <td class="admin-table-meta">{{ editorial.country ?? '—' }}</td>
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(editorial)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(editorial)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!cargando && editoriales.length === 0">
              <td colspan="4" class="text-center py-4 admin-table-empty">
                No hay editoriales registradas
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Editorial
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ editorialEditando ? 'Editar Editorial' : 'Nueva Editorial' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-12">
                <label class="form-label fw-semibold">
                  Nombre <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: Planeta Cómic"
                />
              </div>
              <div class="col-12">
                <label class="form-label fw-semibold">País</label>
                <input
                  v-model="form.country"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: España"
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
              {{ editorialEditando ? 'Guardar Cambios' : 'Crear Editorial' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Editorial
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Editorial</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar la editorial
              <strong>"{{ editorialAEliminar?.name }}"</strong>?
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
