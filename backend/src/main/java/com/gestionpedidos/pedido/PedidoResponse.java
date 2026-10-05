package com.gestionpedidos.pedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        Long id,
        String clienteNombre,
        String clienteEmail,
        EstadoPedido estado,
        BigDecimal total,
        Instant creadoEn,
        List<LineaResponse> lineas) {

    public static PedidoResponse desde(Pedido p) {
        List<LineaResponse> ls = p.getLineas().stream()
                .map(l -> new LineaResponse(l.getId(), l.getDescripcion(), l.getCantidad(), l.getPrecioUnitario()))
                .toList();
        return new PedidoResponse(p.getId(), p.getClienteNombre(), p.getClienteEmail(),
                p.getEstado(), p.getTotal(), p.getCreadoEn(), ls);
    }
}
