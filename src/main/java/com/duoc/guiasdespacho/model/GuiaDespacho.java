package com.duoc.guiasdespacho.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "guias_despacho")
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transportista;

    @Column(nullable = false)
    private LocalDate fecha;

    private String s3Url; // Aqui guardamos la ruta cuando subamos el archivo a AWS S3

    // Constructores vacios y con parametros
    public GuiaDespacho() {}

    public GuiaDespacho(String transportista, LocalDate fecha) {
        this.transportista = transportista;
        this.fecha = fecha;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getS3Url() { return s3Url; }
    public void setS3Url(String s3Url) { this.s3Url = s3Url; }
}