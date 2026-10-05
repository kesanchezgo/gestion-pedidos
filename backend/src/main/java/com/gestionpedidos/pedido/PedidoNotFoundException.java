package com.gestionpedidos.pedido;

/** Lanzada cuando el pedido no existe → 404 ProblemDetail (FR-005). */
public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long id) {
        super("Pedido no encontrado: " + id);
    }
}
