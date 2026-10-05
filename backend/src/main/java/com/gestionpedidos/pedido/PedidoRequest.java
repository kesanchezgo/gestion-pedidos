package com.gestionpedidos.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PedidoRequest(
        @NotBlank(message = "clienteNombre es obligatorio") @Size(max = 120) String clienteNombre,
        @NotBlank(message = "clienteEmail es obligatorio")
        @Email(message = "clienteEmail debe ser un email valido") String clienteEmail,
        @NotEmpty(message = "lineas debe tener al menos 1 elemento")
        List<@Valid LineaRequest> lineas) {
}
