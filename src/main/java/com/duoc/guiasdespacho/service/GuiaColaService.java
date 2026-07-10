package com.duoc.guiasdespacho.service;

import com.duoc.guiasdespacho.config.RabbitMQConfig;
import com.duoc.guiasdespacho.model.GuiaProcesada;
import com.duoc.guiasdespacho.repository.GuiaProcesadaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GuiaColaService {

    private final RabbitTemplate rabbitTemplate;
    private final GuiaProcesadaRepository guiaProcesadaRepository;

    public GuiaColaService(RabbitTemplate rabbitTemplate,
                           GuiaProcesadaRepository guiaProcesadaRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.guiaProcesadaRepository = guiaProcesadaRepository;
    }

    public GuiaProcesada procesarSiguienteMensaje() {
        Object mensajeRecibido = rabbitTemplate.receiveAndConvert(RabbitMQConfig.COLA_GUIAS);

        if (mensajeRecibido == null) {
            return null;
        }

        String mensaje = mensajeRecibido.toString();

        try {
            String[] partes = mensaje.split("\\|");

            if (partes.length != 3) {
                throw new IllegalArgumentException("Formato de mensaje invalido");
            }

            Long guiaOriginalId = Long.parseLong(partes[0]);
            String transportista = partes[1];
            LocalDate fecha = LocalDate.parse(partes[2]);

            GuiaProcesada guiaProcesada = new GuiaProcesada(
                    guiaOriginalId,
                    transportista,
                    fecha,
                    "PROCESADA",
                    LocalDateTime.now(),
                    mensaje
            );

            return guiaProcesadaRepository.save(guiaProcesada);

        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_GUIAS,
                    RabbitMQConfig.ROUTING_ERRORES,
                    mensaje
            );

            System.out.println("Error procesando mensaje. Enviado a cola de errores: " + mensaje);
            return null;
        }
    }
}