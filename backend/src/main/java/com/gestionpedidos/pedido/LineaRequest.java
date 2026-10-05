package com.gestionpedidos.pedido;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LineaRequest(
        @NotBlank(message = "descripcion es obligatoria") String descripcion,
        @Min(value = 1, message = "cantidad debe ser >= 1") Integer cantidad,
        @NotNull(message = "precioUnitario es obligatorio")
        @DecimalMin(value = "0.01", message = "precioUnitario debe ser > 0")
        @Digits(integer = 10, fraction = 2, message = "precioUnitario admite maximo 2 decimales")
        BigDecimal precioUnitario) {
}
