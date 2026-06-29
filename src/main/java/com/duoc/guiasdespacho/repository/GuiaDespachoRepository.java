package com.duoc.guiasdespacho.repository;

import com.duoc.guiasdespacho.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {
    // Este metodo es para el endpoint de consulta por transportista y fecha
    List<GuiaDespacho> findByTransportistaAndFecha(String transportista, LocalDate fecha);
}