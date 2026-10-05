package com.gestionpedidos.pedido;

import java.math.BigDecimal;

public record LineaResponse(Long id, String descripcion, Integer cantidad, BigDecimal precioUnitario) {
}
