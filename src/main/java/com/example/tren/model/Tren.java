package com.example.tren.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "trenes")
public class Tren {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El fabricante es obligatorio")
    @Size(max = 100, message = "El fabricante no puede superar los 100 caracteres")
    private String fabricante;

    @NotEmpty(message = "El modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    private String modelo;

    @NotEmpty(message = "La matrícula es obligatoria")
    @Size(max = 50, message = "La matrícula no puede superar los 50 caracteres")
    private String matricula;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1800, message = "El año no es válido")
    @Column(name = "anio")
    private Integer anio;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio debe ser positivo")
    @Column(columnDefinition = "DECIMAL(12,2)")
    private Double precio;

    @NotNull(message = "La velocidad máxima es obligatoria")
    @Min(value = 0, message = "La velocidad debe ser positiva")
    @Column(name = "velocidad_maxima")
    private Integer velocidadMaxima;

    @NotNull(message = "El tipo de propulsión es obligatorio")
    @Enumerated(EnumType.STRING)
    private Propulsion propulsion;

    @NotNull(message = "El tipo de servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio")
    private TipoServicio tipoServicio;

    private String color;

    public Tren() {
    }

    public Tren(Long id, String fabricante, String modelo, String matricula, Integer anio, Double precio, Integer velocidadMaxima, Propulsion propulsion, TipoServicio tipoServicio, String color) {
        this.id = id;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.matricula = matricula;
        this.anio = anio;
        this.precio = precio;
        this.velocidadMaxima = velocidadMaxima;
        this.propulsion = propulsion;
        this.tipoServicio = tipoServicio;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getVelocidadMaxima() {
        return velocidadMaxima;
    }

    public void setVelocidadMaxima(Integer velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
    }

    public Propulsion getPropulsion() {
        return propulsion;
    }

    public void setPropulsion(Propulsion propulsion) {
        this.propulsion = propulsion;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}