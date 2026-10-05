import type { Pedido } from '../api/cliente'

/** T023 (US2/US3): listado de pedidos con total a 2 decimales. */
export default function ListadoPedidos({ pedidos }: { pedidos: Pedido[] }) {
  if (pedidos.length === 0) {
    return <p>No hay pedidos todavía. Da de alta el primero con el formulario.</p>
  }
  return (
    <table>
      <thead>
        <tr>
          <th>#</th>
          <th>Cliente</th>
          <th>Estado</th>
          <th>Líneas</th>
          <th>Total</th>
        </tr>
      </thead>
      <tbody>
        {pedidos.map((p) => (
          <tr key={p.id}>
            <td>{p.id}</td>
            <td>
              {p.clienteNombre} &lt;{p.clienteEmail}&gt;
            </td>
            <td>{p.estado}</td>
            <td>{p.lineas.length}</td>
            <td>{p.total.toFixed(2)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
