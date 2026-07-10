package com.duoc.guiasdespacho.producer;

import com.duoc.guiasdespacho.config.RabbitMQConfig;
import com.duoc.guiasdespacho.model.GuiaDespacho;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class GuiaProducer {

    private final RabbitTemplate rabbitTemplate;

    public GuiaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarGuiaACola(GuiaDespacho guia) {
        String mensaje = guia.getId() + "|" + guia.getTransportista() + "|" + guia.getFecha();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_GUIAS,
                RabbitMQConfig.ROUTING_GUIAS,
                mensaje
        );

        System.out.println("Guia enviada a cola principal: " + mensaje);
    }

    public void enviarGuiaAColaErrores(GuiaDespacho guia) {
        String mensaje = guia.getId() + "|" + guia.getTransportista() + "|" + guia.getFecha();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_GUIAS,
                RabbitMQConfig.ROUTING_ERRORES,
                mensaje
        );

        System.out.println("Guia enviada a cola de errores: " + mensaje);
    }
}