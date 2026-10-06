package com.example.tren.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Cada fila de esta tabla es una operación (crear, actualizar o eliminar) hecha sobre una entidad
@Entity
@Table(name = "log_transacciones")
public class LogTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperacionLog operacion;

    // Nombre de la entidad afectada, por ejemplo "Tren"
    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(length = 500)
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public LogTransaccion() {
    }

    public LogTransaccion(OperacionLog operacion, String entidad, Long entidadId, String detalle, LocalDateTime fecha) {
        this.operacion = operacion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperacionLog getOperacion() {
        return operacion;
    }

    public void setOperacion(OperacionLog operacion) {
        this.operacion = operacion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
