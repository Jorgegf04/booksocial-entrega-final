<!--
  WorksView.vue — Gestión de Obras (libros, manga, cómics).

  Qué hace esta vista:
    - Lista todas las obras del catálogo en una tabla.
    - Permite crear una nueva obra (modal con formulario).
    - Permite editar una obra existente (mismo modal, relleno con sus datos).
    - Permite eliminar una obra (modal de confirmación).
    - Si se selecciona imagen nueva, la sube primero a POST /api/upload
      y luego usa la URL devuelta en el campo img.

  Servicios usados:
    - worksService (listar, crear, actualizar, eliminar)
    - authorsService (listar — para el selector de autores en el formulario)
    - uploadService (subir — para imágenes de portada)

  Enums hardcodeados (valores del backend):
    - WorkType: BOOK, MANGA, COMIC
    - Genre: ACTION, ADVENTURE, COMEDY…
    - Demographic: SHONEN, SHOJO, SEINEN, JOSEI, KODOMO
-->
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { useToast } from 'vue-toastification'
import { worksService } from '@/api/services/worksService'
import { authorsService } from '@/api/services/authorsService'
import { uploadService } from '@/api/services/uploadService'

const toast = useToast()

// ─── Enums del dominio (valores exactos del backend) ─────────────────────────
const TIPOS = ['BOOK', 'MANGA', 'COMIC']
const GENEROS = [
  'ACTION','ADVENTURE','BIOGRAPHY','COMEDY','CRIME','CYBERPUNK','DARK_FANTASY',
  'DRAMA','ECCHI','FANTASY','HAREM','HISTORICAL','HORROR','ISEKAI','MARTIAL_ARTS',
  'MECHA','MYSTERY','PHILOSOPHICAL','POST_APOCALYPTIC','PSYCHOLOGICAL','ROMANCE',
  'SCHOOL','SCIENCE_FICTION','SLICE_OF_LIFE','SPORTS','SUPERHERO','SUPERNATURAL',
  'SUSPENSE','THRILLER','TRAGEDY','WAR',
]
const DEMOGRAFIAS = ['SHONEN', 'SHOJO', 'SEINEN', 'JOSEI', 'KODOMO']

// ─── Estado ───────────────────────────────────────────────────────────────────
const obras = ref([])
const autores = ref([])
const cargando = ref(false)
const guardando = ref(false)

// Obra que se está editando (null → modo crear)
const obraEditando = ref(null)

// Formulario compartido para crear y editar
const form = reactive({
  title: '',
  description: '',
  type: '',
  genre: '',
  demographic: '',
  publicationDate: '',
  img: '',
  averageRating: '',
  authors: [], // lista de nombres
})

// Archivo de imagen seleccionado (no puede ir en reactive)
const imgFileSeleccionado = ref(null)

// Obra a eliminar
const obraAEliminar = ref(null)
const eliminando = ref(false)

// ─── Referencias a modales Bootstrap ─────────────────────────────────────────
const refModalForm = ref(null)
const refModalEliminar = ref(null)
let bsModalForm, bsModalEliminar

onMounted(async () => {
  // Inicializa las instancias de Bootstrap Modal
  bsModalForm = new Modal(refModalForm.value)
  bsModalEliminar = new Modal(refModalEliminar.value)
  await cargarDatos()
})

// ─── Carga inicial de datos ───────────────────────────────────────────────────
async function cargarDatos() {
  cargando.value = true
  try {
    // Cargamos obras y autores en paralelo
    const [obrasData, autoresData] = await Promise.all([
      worksService.listar(),
      authorsService.listar(),
    ])
    obras.value = obrasData
    autores.value = autoresData
  } catch {
    toast.error('No se pudieron cargar las obras.')
  } finally {
    cargando.value = false
  }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
/** Formatea un nombre de enum (SLICE_OF_LIFE → Slice Of Life) */
function formatEnum(valor) {
  if (!valor) return '—'
  return valor
    .split('_')
    .map((p) => p.charAt(0) + p.slice(1).toLowerCase())
    .join(' ')
}

/** Resetea el formulario a valores vacíos */
function resetForm() {
  Object.assign(form, {
    title: '', description: '', type: '', genre: '',
    demographic: '', publicationDate: '', img: '',
    averageRating: '', authors: [],
  })
  imgFileSeleccionado.value = null
  // Limpia el input file visualmente
  const inp = document.getElementById('wImgFile')
  if (inp) inp.value = ''
}

// ─── Abrir modales ────────────────────────────────────────────────────────────
function abrirModalCrear() {
  obraEditando.value = null
  resetForm()
  bsModalForm.show()
}

function abrirModalEditar(obra) {
  obraEditando.value = obra
  Object.assign(form, {
    title: obra.title ?? '',
    description: obra.description ?? '',
    type: obra.type ?? '',
    genre: obra.genre ?? '',
    demographic: obra.demographic ?? '',
    publicationDate: obra.publicationDate ?? '',
    img: obra.img ?? '',
    averageRating: obra.averageRating ?? '',
    authors: obra.authors ? [...obra.authors] : [],
  })
  imgFileSeleccionado.value = null
  bsModalForm.show()
}

function abrirModalEliminar(obra) {
  obraAEliminar.value = obra
  bsModalEliminar.show()
}

// ─── Guardar (crear o editar) ─────────────────────────────────────────────────
async function guardar() {
  if (!form.title.trim()) {
    toast.warning('El título es obligatorio.')
    return
  }
  guardando.value = true
  try {
    // Si hay imagen nueva, la subimos primero
    if (imgFileSeleccionado.value) {
      form.img = await uploadService.subir(imgFileSeleccionado.value)
    }

    // Construimos el payload limpio (filtramos campos vacíos opcionales)
    const payload = {
      title: form.title.trim(),
      description: form.description || null,
      type: form.type || null,
      genre: form.genre || null,
      demographic: form.demographic || null,
      publicationDate: form.publicationDate || null,
      img: form.img || null,
      averageRating: form.averageRating !== '' ? Number(form.averageRating) : null,
      authors: form.authors,
    }

    if (obraEditando.value) {
      // Editar obra existente
      const actualizada = await worksService.actualizar(obraEditando.value.id, payload)
      const idx = obras.value.findIndex((o) => o.id === obraEditando.value.id)
      if (idx !== -1) obras.value[idx] = actualizada
      toast.success(`"${payload.title}" actualizada correctamente.`)
    } else {
      // Crear nueva obra
      const nueva = await worksService.crear(payload)
      obras.value.unshift(nueva)
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
  if (!obraAEliminar.value) return
  eliminando.value = true
  try {
    await worksService.eliminar(obraAEliminar.value.id)
    obras.value = obras.value.filter((o) => o.id !== obraAEliminar.value.id)
    toast.success(`"${obraAEliminar.value.title}" eliminada.`)
    bsModalEliminar.hide()
  } catch {
    // El interceptor ya muestra el toast
  } finally {
    eliminando.value = false
    obraAEliminar.value = null
  }
}
</script>

<template>
  <div>
    <!-- Topbar -->
    <div class="admin-topbar">
      <div>
        <h1 class="admin-page-title">Obras</h1>
        <p class="admin-page-subtitle">
          Catálogo completo de libros, manga y cómics
        </p>
      </div>
      <button class="btn btn-sm btn-primary d-flex align-items-center gap-1" @click="abrirModalCrear">
        <span class="material-symbols-outlined admin-topbar-btn-icon">add</span>
        Nueva Obra
      </button>
    </div>

    <!-- Tabla -->
    <div class="admin-card">
      <div class="admin-card-header">
        <p class="admin-card-title">
          Todas las obras
          <span class="admin-table-count">({{ obras.length }} total)</span>
        </p>
      </div>

      <!-- Spinner de carga -->
      <div v-if="cargando" class="text-center py-4">
        <div class="spinner-border text-secondary" role="status"></div>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Portada</th>
              <th>Título</th>
              <th>Tipo</th>
              <th>Género</th>
              <th>Demografía</th>
              <th>Publicación</th>
              <th>Rating</th>
              <th>Autores</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="obra in obras" :key="obra.id">
              <!-- Miniatura de portada -->
              <td>
                <div
                  class="admin-work-thumb"
                  :style="obra.img ? `background-image:url(${obra.img})` : ''"
                >
                  <span
                    v-if="!obra.img"
                    class="material-symbols-outlined admin-table-thumb-icon"
                  >menu_book</span>
                </div>
              </td>
              <td>
                <span class="admin-table-name">{{ obra.title }}</span>
              </td>
              <td>
                <span v-if="obra.type" class="admin-badge">{{ obra.type }}</span>
                <span v-else class="admin-table-empty">—</span>
              </td>
              <td class="admin-table-meta">{{ formatEnum(obra.genre) }}</td>
              <td class="admin-table-meta">{{ formatEnum(obra.demographic) }}</td>
              <td class="admin-table-meta">{{ obra.publicationDate ?? '—' }}</td>
              <td>
                <span v-if="obra.averageRating != null" class="admin-work-rating">
                  {{ Number(obra.averageRating).toFixed(1) }} ★
                </span>
                <span v-else class="admin-work-rating-empty">—</span>
              </td>
              <td class="admin-table-meta">
                {{ obra.authors?.join(', ') || '—' }}
              </td>
              <!-- Acciones -->
              <td>
                <div class="d-flex gap-1">
                  <button
                    class="btn btn-sm btn-outline-secondary p-1"
                    title="Editar"
                    @click="abrirModalEditar(obra)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">edit</span>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger p-1"
                    title="Eliminar"
                    @click="abrirModalEliminar(obra)"
                  >
                    <span class="material-symbols-outlined admin-table-action-icon">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <!-- Fila vacía -->
            <tr v-if="!cargando && obras.length === 0">
              <td colspan="9" class="text-center py-4 admin-table-empty">
                No hay obras registradas
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Crear / Editar Obra (compartido)
         ===================================================================== -->
    <div ref="refModalForm" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              {{ obraEditando ? 'Editar Obra' : 'Nueva Obra' }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <div class="modal-body">
            <div class="row g-3">
              <!-- Título -->
              <div class="col-md-8">
                <label class="form-label fw-semibold">
                  Título <span class="text-danger">*</span>
                </label>
                <input
                  v-model="form.title"
                  type="text"
                  class="form-control form-control-sm"
                  placeholder="Ej: Naruto"
                />
              </div>
              <!-- Tipo -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Tipo</label>
                <select v-model="form.type" class="form-select form-select-sm">
                  <option value="">— Sin tipo —</option>
                  <option v-for="t in TIPOS" :key="t" :value="t">{{ t }}</option>
                </select>
              </div>
              <!-- Descripción -->
              <div class="col-12">
                <label class="form-label fw-semibold">Descripción</label>
                <textarea
                  v-model="form.description"
                  class="form-control form-control-sm"
                  rows="3"
                ></textarea>
              </div>
              <!-- Género -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Género</label>
                <select v-model="form.genre" class="form-select form-select-sm">
                  <option value="">— Sin género —</option>
                  <option v-for="g in GENEROS" :key="g" :value="g">
                    {{ formatEnum(g) }}
                  </option>
                </select>
              </div>
              <!-- Demografía -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Demografía</label>
                <select v-model="form.demographic" class="form-select form-select-sm">
                  <option value="">— Sin demografía —</option>
                  <option v-for="d in DEMOGRAFIAS" :key="d" :value="d">{{ d }}</option>
                </select>
              </div>
              <!-- Fecha publicación -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Fecha publicación</label>
                <input
                  v-model="form.publicationDate"
                  type="date"
                  class="form-control form-control-sm"
                />
              </div>
              <!-- Imagen de portada -->
              <div class="col-md-8">
                <label class="form-label fw-semibold">Imagen de portada</label>
                <input
                  id="wImgFile"
                  type="file"
                  accept="image/*"
                  class="form-control form-control-sm"
                  @change="imgFileSeleccionado = $event.target.files[0] || null"
                />
                <!-- URL de imagen actual en modo edición -->
                <div v-if="obraEditando && form.img" class="mt-1 admin-img-preview">
                  Actual: {{ form.img }}
                </div>
              </div>
              <!-- Rating -->
              <div class="col-md-4">
                <label class="form-label fw-semibold">Rating</label>
                <input
                  v-model="form.averageRating"
                  type="number"
                  class="form-control form-control-sm"
                  step="0.1"
                  min="0"
                  max="10"
                  placeholder="ej: 8.5"
                />
              </div>
              <!-- Autores (multiselect por nombre) -->
              <div class="col-12">
                <label class="form-label fw-semibold">Autores</label>
                <select
                  v-model="form.authors"
                  class="form-select form-select-sm"
                  multiple
                  size="4"
                >
                  <option
                    v-for="autor in autores"
                    :key="autor.id"
                    :value="autor.name"
                  >
                    {{ autor.name }}
                  </option>
                </select>
                <div class="form-text">Mantén Ctrl/⌘ para seleccionar varios autores</div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-sm btn-secondary"
              data-bs-dismiss="modal"
            >Cancelar</button>
            <button
              type="button"
              class="btn btn-sm btn-primary"
              :disabled="guardando"
              @click="guardar"
            >
              <span
                v-if="guardando"
                class="spinner-border spinner-border-sm me-1"
                role="status"
              ></span>
              {{ obraEditando ? 'Guardar Cambios' : 'Crear Obra' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- =====================================================================
         MODAL: Eliminar Obra
         ===================================================================== -->
    <div ref="refModalEliminar" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Eliminar Obra</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p class="admin-modal-confirm">
              ¿Seguro que quieres eliminar
              <strong>"{{ obraAEliminar?.title }}"</strong>?
              Esta acción no se puede deshacer.
            </p>
          </div>
          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-sm btn-secondary"
              data-bs-dismiss="modal"
            >Cancelar</button>
            <button
              type="button"
              class="btn btn-sm btn-danger"
              :disabled="eliminando"
              @click="confirmarEliminar"
            >
              <span
                v-if="eliminando"
                class="spinner-border spinner-border-sm me-1"
                role="status"
              ></span>
              Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
