package com.gestionpedidos.pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository repositorio;

    public PedidoService(PedidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public PedidoResponse crear(PedidoRequest req) {
        Pedido pedido = new Pedido();
        pedido.setClienteNombre(req.clienteNombre().trim());
        pedido.setClienteEmail(req.clienteEmail().trim());
        for (LineaRequest lr : req.lineas()) {
            pedido.agregarLinea(new LineaPedido(lr.descripcion().trim(), lr.cantidad(), lr.precioUnitario()));
        }
        // FR-002: el total se calcula aquí y en @PrePersist; el del request no existe en el DTO
        pedido.setTotal(pedido.calcularTotal());
        Pedido guardado = repositorio.save(pedido);
        return PedidoResponse.desde(guardado);
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id) {
        Pedido pedido = repositorio.findById(id).orElseThrow(() -> new PedidoNotFoundException(id));
        return PedidoResponse.desde(pedido);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(int page, int size) {
        PageRequest paginacion = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
        return repositorio.findAll(paginacion).map(PedidoResponse::desde);
    }
}
