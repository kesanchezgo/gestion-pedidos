package com.gestionpedidos.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** T010 (FR-007): invariantes de cálculo — debe existir antes que el servicio. */
class PedidoTest {

    private LineaPedido linea(String descripcion, int cantidad, String precio) {
        return new LineaPedido(descripcion, cantidad, new BigDecimal(precio));
    }

    @Test
    void totalEsLaSumaDeLineasConEscalaDos() {
        Pedido p = new Pedido();
        p.agregarLinea(linea("Cafe en grano", 2, "9.90"));
        p.agregarLinea(linea("Taza", 1, "14.50"));
        assertEquals(new BigDecimal("34.30"), p.calcularTotal());
    }

    @Test
    void totalRedondeaDosDecimalesHalfUp() {
        Pedido p = new Pedido();
        p.agregarLinea(linea("Borde", 1, "0.005"));
        assertEquals(new BigDecimal("0.01"), p.calcularTotal());
    }

    @Test
    void pedidoSinLineasDaCero() {
        assertEquals(new BigDecimal("0.00"), new Pedido().calcularTotal());
    }
}
