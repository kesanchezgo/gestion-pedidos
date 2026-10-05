import '@testing-library/jest-dom/vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { describe, expect, it, vi, beforeEach } from 'vitest'
import FormularioAlta from './FormularioAlta'
import { crearPedido, ErrorValidacion } from '../api/cliente'

vi.mock('../api/cliente', () => ({
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

const crear = vi.mocked(crearPedido)

function enviarFormulario() {
  fireEvent.submit(screen.getByRole('form', { name: 'alta-pedido' }))
}

/** T019 (FR-006/FR-007): validación en cliente y reflejo de errores del backend. */
describe('FormularioAlta', () => {
  beforeEach(() => {
    crear.mockReset()
  })

  it('no envía con email inválido y muestra el error en el campo', async () => {
    const onCreado = vi.fn()
    render(<FormularioAlta onPedidoCreado={onCreado} />)

    fireEvent.change(screen.getByLabelText('Email del cliente'), {
      target: { value: 'no-es-un-email' },
    })
    enviarFormulario()

    expect(crear).not.toHaveBeenCalled()
    expect(await screen.findByText('clienteEmail debe ser un email valido')).toBeInTheDocument()
  })

  it('no envía con línea de cantidad 0', async () => {
    const onCreado = vi.fn()
    render(<FormularioAlta onPedidoCreado={onCreado} />)

    fireEvent.change(screen.getByLabelText('Nombre del cliente'), { target: { value: 'Ana' } })
    fireEvent.change(screen.getByLabelText('Email del cliente'), { target: { value: 'ana@ej.com' } })
    fireEvent.change(screen.getByLabelText('Descripción línea 1'), { target: { value: 'Cafe' } })
    fireEvent.change(screen.getByLabelText('Cantidad línea 1'), { target: { value: '0' } })
    enviarFormulario()

    expect(crear).not.toHaveBeenCalled()
    expect(await screen.findByText('cantidad debe ser >= 1')).toBeInTheDocument()
  })

  it('envía el pedido válido, muestra el total del backend y avisa', async () => {
    crear.mockResolvedValue({
      id: 7,
      clienteNombre: 'Ana',
      clienteEmail: 'ana@ej.com',
      estado: 'ABIERTO',
      total: 34.3,
      creadoEn: '2026-10-04T12:00:00Z',
      lineas: [],
    })
    const onCreado = vi.fn()
    render(<FormularioAlta onPedidoCreado={onCreado} />)

    fireEvent.change(screen.getByLabelText('Nombre del cliente'), { target: { value: 'Ana' } })
    fireEvent.change(screen.getByLabelText('Email del cliente'), { target: { value: 'ana@ej.com' } })
    fireEvent.change(screen.getByLabelText('Descripción línea 1'), { target: { value: 'Cafe' } })
    fireEvent.change(screen.getByLabelText('Precio línea 1'), { target: { value: '9.9' } })
    enviarFormulario()

    await waitFor(() => expect(crear).toHaveBeenCalledTimes(1))
    expect(onCreado).toHaveBeenCalledTimes(1)
    expect(await screen.findByText(/Pedido #7 creado/)).toBeInTheDocument()
  })

  it('refleja el error por campo que devuelve el backend (400 ProblemDetail)', async () => {
    crear.mockRejectedValue(
      new ErrorValidacion({ 'lineas[0].descripcion': 'descripcion es obligatoria' }),
    )
    const onCreado = vi.fn()
    render(<FormularioAlta onPedidoCreado={onCreado} />)

    fireEvent.change(screen.getByLabelText('Nombre del cliente'), { target: { value: 'Ana' } })
    fireEvent.change(screen.getByLabelText('Email del cliente'), { target: { value: 'ana@ej.com' } })
    enviarFormulario()

    expect(await screen.findByText('descripcion es obligatoria')).toBeInTheDocument()
    expect(onCreado).not.toHaveBeenCalled()
  })
})
