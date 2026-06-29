package com.duoc.guiasdespacho.controller;

import com.duoc.guiasdespacho.model.GuiaDespacho;
import com.duoc.guiasdespacho.repository.GuiaDespachoRepository;
import com.duoc.guiasdespacho.service.S3Service;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GuiaDespachoController.class)
@AutoConfigureMockMvc(addFilters = false) // Apaga Azure B2C solo para este test
public class GuiaDespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuiaDespachoRepository repository; // Simulamos la base de datos

    @MockBean
    private S3Service s3Service; // Simulamos AWS

    @Test
    public void testConsultarGuias_Exito() throws Exception {
        // 1 Creamos datos falsos
        GuiaDespacho guiaFalsa = new GuiaDespacho();
        guiaFalsa.setId(1L);
        guiaFalsa.setTransportista("FastDelivery");
        guiaFalsa.setFecha(LocalDate.parse("2026-06-28"));

        // Le decimos al repositorio simulado que debe responder
        Mockito.when(repository.findByTransportistaAndFecha("FastDelivery", LocalDate.parse("2026-06-28")))
               .thenReturn(Arrays.asList(guiaFalsa));

        // 2 Simulamos una peticion GET del cliente
        mockMvc.perform(get("/api/guias/buscar")
                .param("transportista", "FastDelivery")
                .param("fecha", "2026-06-28")
                .contentType(MediaType.APPLICATION_JSON))
                
        // 3 Confirmamos que responda 200 OK y traiga el transportista correcto
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transportista").value("FastDelivery"));
    }

    @Test
    public void testCrearGuia_Exito() throws Exception {
        // 1 Preparamos la guia que el cliente va a enviar
        GuiaDespacho nuevaGuia = new GuiaDespacho();
        nuevaGuia.setTransportista("Starken");
        nuevaGuia.setFecha(LocalDate.parse("2026-06-28"));

        // Le decimos al repositorio simulado que cuando guarde, devuelva la guia con un ID asignado
        GuiaDespacho guiaGuardada = new GuiaDespacho();
        guiaGuardada.setId(2L);
        guiaGuardada.setTransportista("Starken");
        guiaGuardada.setFecha(LocalDate.parse("2026-06-28"));

        Mockito.when(repository.save(Mockito.any(GuiaDespacho.class))).thenReturn(guiaGuardada);

        // 2 Simulamos un POST con el JSON de la guia
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/guias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"transportista\": \"Starken\", \"fecha\": \"2026-06-28\"}"))
                
        // 3 Confirmamos que responda 201 Created y devuelva los datos
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.transportista").value("Starken"));
    }
}