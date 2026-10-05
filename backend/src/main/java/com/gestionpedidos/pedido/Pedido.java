package com.gestionpedidos.pedido;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_nombre", nullable = false, length = 120)
    private String clienteNombre;

    @Column(name = "cliente_email", nullable = false, length = 254)
    private String clienteEmail;

    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.ABIERTO;

    /** FR-002: total SIEMPRE calculado en servidor; el valor del cliente se ignora. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<LineaPedido> lineas = new ArrayList<>();

    @PrePersist
    void antesDeCrear() {
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
        total = calcularTotal();
    }

    /** Σ(cantidad × precioUnitario), escala 2, HALF_UP (research D2). */
    @Transient
    public BigDecimal calcularTotal() {
        BigDecimal suma = BigDecimal.ZERO;
        for (LineaPedido linea : lineas) {
            suma = suma.add(linea.subtotal());
        }
        return suma.setScale(2, RoundingMode.HALF_UP);
    }

    public void agregarLinea(LineaPedido linea) {
        linea.setPedido(this);
        lineas.add(linea);
    }

    public Long getId() {
        return id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }
}
