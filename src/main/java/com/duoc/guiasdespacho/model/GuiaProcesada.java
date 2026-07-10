package com.duoc.guiasdespacho.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guias_procesadas")
public class GuiaProcesada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long guiaOriginalId;

    private String transportista;

    private LocalDate fecha;

    private String estadoProcesamiento;

    private LocalDateTime fechaProcesamiento;

    @Column(length = 500)
    private String mensajeOriginal;

    public GuiaProcesada() {
    }

    public GuiaProcesada(Long guiaOriginalId, String transportista, LocalDate fecha,
                         String estadoProcesamiento, LocalDateTime fechaProcesamiento,
                         String mensajeOriginal) {
        this.guiaOriginalId = guiaOriginalId;
        this.transportista = transportista;
        this.fecha = fecha;
        this.estadoProcesamiento = estadoProcesamiento;
        this.fechaProcesamiento = fechaProcesamiento;
        this.mensajeOriginal = mensajeOriginal;
    }

    public Long getId() {
        return id;
    }

    public Long getGuiaOriginalId() {
        return guiaOriginalId;
    }

    public void setGuiaOriginalId(Long guiaOriginalId) {
        this.guiaOriginalId = guiaOriginalId;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstadoProcesamiento() {
        return estadoProcesamiento;
    }

    public void setEstadoProcesamiento(String estadoProcesamiento) {
        this.estadoProcesamiento = estadoProcesamiento;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public String getMensajeOriginal() {
        return mensajeOriginal;
    }

    public void setMensajeOriginal(String mensajeOriginal) {
        this.mensajeOriginal = mensajeOriginal;
    }
}