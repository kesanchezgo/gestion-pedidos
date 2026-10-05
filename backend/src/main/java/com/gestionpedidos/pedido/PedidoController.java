package com.gestionpedidos.pedido;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Contrato: specs/0001-alta-pedido/contracts/pedidos-api.yaml */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servicio;

    public PedidoController(PedidoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = servicio.crear(request);
        return ResponseEntity.created(URI.create("/api/pedidos/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @GetMapping
    public Page<PedidoResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return servicio.listar(page, Math.min(size, 100));
    }
}
