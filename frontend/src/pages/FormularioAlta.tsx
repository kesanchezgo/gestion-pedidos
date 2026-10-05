import { useState } from 'react'
import { crearPedido, ErrorValidacion, type LineaPayload } from '../api/cliente'

interface Props {
  onPedidoCreado: () => void
}

const LINEA_VACIA: LineaPayload = { descripcion: '', cantidad: 1, precioUnitario: 0 }

/** T022 (US3/FR-006): alta con validación por campo (cliente y reflejo del backend). */
export default function FormularioAlta({ onPedidoCreado }: Props) {
  const [clienteNombre, setClienteNombre] = useState('')
  const [clienteEmail, setClienteEmail] = useState('')
  const [lineas, setLineas] = useState<LineaPayload[]>([{ ...LINEA_VACIA }])
  const [errores, setErrores] = useState<Record<string, string>>({})
  const [aviso, setAviso] = useState('')
  const [enviando, setEnviando] = useState(false)

  /** Mismos límites que el backend (FR-003): es UX, no seguridad. */
  function validar(): Record<string, string> {
    const e: Record<string, string> = {}
    if (!clienteNombre.trim()) {
      e.clienteNombre = 'clienteNombre es obligatorio'
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(clienteEmail)) {
      e.clienteEmail = 'clienteEmail debe ser un email valido'
    }
    lineas.forEach((l, i) => {
      if (!l.descripcion.trim()) {
        e[`lineas[${i}].descripcion`] = 'descripcion es obligatoria'
      }
      if (l.cantidad < 1) {
        e[`lineas[${i}].cantidad`] = 'cantidad debe ser >= 1'
      }
      if (!(l.precioUnitario > 0)) {
        e[`lineas[${i}].precioUnitario`] = 'precioUnitario debe ser > 0'
      }
    })
    return e
  }

  async function enviar(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault()
    setErrores({})
    setAviso('')
    const erroresCliente = validar()
    if (Object.keys(erroresCliente).length > 0) {
      setErrores(erroresCliente)
      return
    }
    setEnviando(true)
    try {
      const creado = await crearPedido({
        clienteNombre: clienteNombre.trim(),
        clienteEmail: clienteEmail.trim(),
        lineas,
      })
      setAviso(`Pedido #${creado.id} creado — total ${creado.total.toFixed(2)}`)
      setClienteNombre('')
      setClienteEmail('')
      setLineas([{ ...LINEA_VACIA }])
      onPedidoCreado()
    } catch (err) {
      if (err instanceof ErrorValidacion) {
        setErrores(err.errores)
      } else {
        setErrores({ global: err instanceof Error ? err.message : String(err) })
      }
    } finally {
      setEnviando(false)
    }
  }

  function errorCampo(clave: string) {
    return errores[clave] ? <span role="alert">{errores[clave]}</span> : null
  }

  return (
    <form onSubmit={enviar} aria-label="alta-pedido">
      <div>
        <label htmlFor="cliente-nombre">Nombre del cliente</label>
        <input
          id="cliente-nombre"
          value={clienteNombre}
          onChange={(e) => setClienteNombre(e.target.value)}
        />
        {errorCampo('clienteNombre')}
      </div>
      <div>
        <label htmlFor="cliente-email">Email del cliente</label>
        <input
          id="cliente-email"
          type="email"
          value={clienteEmail}
          onChange={(e) => setClienteEmail(e.target.value)}
        />
        {errorCampo('clienteEmail')}
      </div>

      {lineas.map((linea, i) => (
        <fieldset key={i}>
          <label htmlFor={`linea-desc-${i}`}>{`Descripción línea ${i + 1}`}</label>
          <input
            id={`linea-desc-${i}`}
            value={linea.descripcion}
            onChange={(e) =>
              setLineas(lineas.map((l, j) => (j === i ? { ...l, descripcion: e.target.value } : l)))
            }
          />
          {errorCampo(`lineas[${i}].descripcion`)}
          <label htmlFor={`linea-cant-${i}`}>{`Cantidad línea ${i + 1}`}</label>
          <input
            id={`linea-cant-${i}`}
            type="number"
            min={1}
            value={linea.cantidad}
            onChange={(e) =>
              setLineas(
                lineas.map((l, j) =>
                  j === i ? { ...l, cantidad: Number(e.target.value) } : l,
                ),
              )
            }
          />
          {errorCampo(`lineas[${i}].cantidad`)}
          <label htmlFor={`linea-precio-${i}`}>{`Precio línea ${i + 1}`}</label>
          <input
            id={`linea-precio-${i}`}
            type="number"
            step="0.01"
            min="0.01"
            value={linea.precioUnitario}
            onChange={(e) =>
              setLineas(
                lineas.map((l, j) =>
                  j === i ? { ...l, precioUnitario: Number(e.target.value) } : l,
                ),
              )
            }
          />
          {errorCampo(`lineas[${i}].precioUnitario`)}
        </fieldset>
      ))}

      <button type="button" onClick={() => setLineas([...lineas, { ...LINEA_VACIA }])}>
        Añadir línea
      </button>
      <button type="submit" disabled={enviando}>
        Guardar pedido
      </button>
      {errores.global ? <p role="alert">{errores.global}</p> : null}
      {aviso ? <p role="status">{aviso}</p> : null}
    </form>
  )
}
