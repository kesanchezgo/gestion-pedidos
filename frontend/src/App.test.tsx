import '@testing-library/jest-dom/vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi, beforeEach } from 'vitest'
import App from './App'
import { listarPedidos } from './api/cliente'

vi.mock('./api/cliente', () => ({
  crearPedido: vi.fn(),
  listarPedidos: vi.fn(),
  ErrorValidacion: class extends Error {
    errores: Record<string, string>
    constructor(errores: Record<string, string>) {
      super('validacion')
      this.errores = errores
    }
  },
}))

const listar = vi.mocked(listarPedidos)

const PEDIDO_1 = {
  id: 1,
  clienteNombre: 'Ana',
  clienteEmail: 'ana@ej.com',
  estado: 'ABIERTO',
  total: 34.3,
  creadoEn: '2026-10-04T12:00:00Z',
  lineas: [],
}

/** T020 (US3/FR-006): el listado carga desde el API y refleja el total. */
describe('App - listado', () => {
  beforeEach(() => {
    listar.mockReset()
    listar.mockResolvedValue({ content: [PEDIDO_1], number: 0, size: 10, totalElements: 1, totalPages: 1 })
  })

  it('carga los pedidos al montar y muestra el total con dos decimales', async () => {
    render(<App />)
    await waitFor(() => expect(listar).toHaveBeenCalledTimes(1))
    expect(await screen.findByText(/Ana/)).toBeInTheDocument()
    expect(screen.getByText('34.30')).toBeInTheDocument()
  })
})
