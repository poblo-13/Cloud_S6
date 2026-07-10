package com.duoc.guiasdespacho.controller;

import com.duoc.guiasdespacho.model.GuiaDespacho;
import com.duoc.guiasdespacho.repository.GuiaDespachoRepository;
import com.duoc.guiasdespacho.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.duoc.guiasdespacho.producer.GuiaProducer;
import com.duoc.guiasdespacho.model.GuiaProcesada;
import com.duoc.guiasdespacho.service.GuiaColaService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    @Autowired
    private GuiaDespachoRepository repository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private GuiaProducer guiaProducer;

    @Autowired
    private GuiaColaService guiaColaService;

    // 1. Crear guías de despacho — Solo ROLE_admin
    @PostMapping
    public ResponseEntity<GuiaDespacho> crearGuia(@RequestBody GuiaDespacho guia) {
        GuiaDespacho nuevaGuia = repository.save(guia);
        guiaProducer.enviarGuiaACola(nuevaGuia);
        return ResponseEntity.status(201).body(nuevaGuia);
    }

    // 2. Modificar o actualizar guías — Solo ROLE_admin
    @PutMapping("/{id}")
    public ResponseEntity<GuiaDespacho> actualizarGuia(@PathVariable Long id, @RequestBody GuiaDespacho guiaActualizada) {
        return repository.findById(id)
                .map(guia -> {
                    guia.setTransportista(guiaActualizada.getTransportista());
                    guia.setFecha(guiaActualizada.getFecha());
                    return ResponseEntity.ok(repository.save(guia));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. Eliminar guías específicas — Solo ROLE_admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGuia(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Consultar guías por transportista y fecha — ROLE_consulta y ROLE_admin
    @GetMapping("/buscar")
    public ResponseEntity<List<GuiaDespacho>> consultarGuias(
            @RequestParam String transportista,
            @RequestParam String fecha) {
        LocalDate fechaParsed = LocalDate.parse(fecha);
        List<GuiaDespacho> resultados = repository.findByTransportistaAndFecha(transportista, fechaParsed);
        return ResponseEntity.ok(resultados);
    }

    // 5. Subir guías generadas a S3 — Solo ROLE_admin
    @PostMapping("/{id}/subir")
    public ResponseEntity<String> subirGuiaS3(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return repository.findById(id)
                .map(guia -> {
                    try {
                        String url = s3Service.subirArchivo(file, id);
                        guia.setS3Url(url);
                        repository.save(guia);
                        return ResponseEntity.ok("Archivo subido exitosamente. URL: " + url);
                    } catch (IOException e) {
                        return ResponseEntity.status(500).<String>body("Error al subir el archivo: " + e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().<String>build());
    }

    // 6. Descargar guías con validación de permisos — ROLE_consulta y ROLE_admin
    @GetMapping("/descargar/{id}")
    public ResponseEntity<String> descargarGuiaS3(@PathVariable Long id, @RequestParam String nombreArchivo) {
        return repository.findById(id)
                .map(guia -> {
                    String urlDescarga = s3Service.generarUrlDescarga(id, nombreArchivo);
                    return ResponseEntity.ok("URL de descarga (válida 15 min): " + urlDescarga);
                })
                .orElseGet(() -> ResponseEntity.notFound().<String>build());
    }

    // 7. Procesar mensaje desde cola.guias y guardar en tabla guias_procesadas — Solo ROLE_admin
    @PostMapping("/procesar-cola")
    public ResponseEntity<?> procesarCola() {
        GuiaProcesada guiaProcesada = guiaColaService.procesarSiguienteMensaje();

        if (guiaProcesada == null) {
            return ResponseEntity.ok("No hay mensajes pendientes en cola.guias o el mensaje fue enviado a cola de errores.");
        }

        return ResponseEntity.ok(guiaProcesada);
    }
}
