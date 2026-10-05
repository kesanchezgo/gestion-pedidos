export interface LineaPayload {
  descripcion: string
  cantidad: number
  precioUnitario: number
}

export interface Pedido {
  id: number
  clienteNombre: string
  clienteEmail: string
  estado: string
  total: number
  creadoEn: string
  lineas: { id: number; descripcion: string; cantidad: number; precioUnitario: number }[]
}

export interface PagePedidos {
  content: Pedido[]
  number: number
  size: number
  totalElements: number
  totalPages: number
}

/** Errores 400 del backend (ProblemDetail → properties.errors, FR-003/FR-006). */
export class ErrorValidacion extends Error {
  errores: Record<string, string>
  constructor(errores: Record<string, string>) {
    super('Validacion fallida')
    this.errores = errores
  }
}

const BASE = import.meta.env.VITE_API_BASE ?? '/api'

export async function crearPedido(datos: {
  clienteNombre: string
  clienteEmail: string
  lineas: LineaPayload[]
}): Promise<Pedido> {
  const respuesta = await fetch(`${BASE}/pedidos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  })
  if (respuesta.status === 400) {
    const detalle = (await respuesta.json()) as { errors?: Record<string, string> }
    throw new ErrorValidacion(detalle.errors ?? {})
  }
  if (!respuesta.ok) {
    throw new Error(`Error inesperado del servidor (HTTP ${respuesta.status})`)
  }
  return (await respuesta.json()) as Pedido
}

export async function listarPedidos(page = 0, size = 10): Promise<PagePedidos> {
  const respuesta = await fetch(`${BASE}/pedidos?page=${page}&size=${size}`)
  if (!respuesta.ok) {
    throw new Error(`Error inesperado del servidor (HTTP ${respuesta.status})`)
  }
  return (await respuesta.json()) as PagePedidos
}
