import { useCallback, useEffect, useState } from 'react'
import { listarPedidos, type Pedido } from './api/cliente'
import FormularioAlta from './pages/FormularioAlta'
import ListadoPedidos from './pages/ListadoPedidos'

/** T024 (US3): alta → refresco del listado con el total calculado por el servidor. */
export default function App() {
  const [pedidos, setPedidos] = useState<Pedido[]>([])
  const [error, setError] = useState('')

  const cargar = useCallback(async () => {
    try {
      const pagina = await listarPedidos()
      setPedidos(pagina.content)
      setError('')
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e))
    }
  }, [])

  useEffect(() => {
    void cargar()
  }, [cargar])

  return (
    <main>
      <h1>Gestión de pedidos</h1>
      <FormularioAlta onPedidoCreado={() => void cargar()} />
      {error ? <p role="alert">{error}</p> : null}
      <ListadoPedidos pedidos={pedidos} />
    </main>
  )
}
