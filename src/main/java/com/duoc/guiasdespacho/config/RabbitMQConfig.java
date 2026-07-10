package com.duoc.guiasdespacho.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.boot.ApplicationRunner;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_GUIAS = "cola.guias";
    public static final String COLA_ERRORES = "cola.guias.errores";

    public static final String EXCHANGE_GUIAS = "exchange.guias";

    public static final String ROUTING_GUIAS = "routing.guias";
    public static final String ROUTING_ERRORES = "routing.guias.errores";

    @Bean
    public Queue colaGuias() {
        return new Queue(COLA_GUIAS, true);
    }

    @Bean
    public Queue colaErrores() {
        return new Queue(COLA_ERRORES, true);
    }

    @Bean
    public DirectExchange exchangeGuias() {
        return new DirectExchange(EXCHANGE_GUIAS);
    }

    @Bean
    public Binding bindingGuias() {
        return BindingBuilder
                .bind(colaGuias())
                .to(exchangeGuias())
                .with(ROUTING_GUIAS);
    }

    @Bean
    public Binding bindingErrores() {
        return BindingBuilder
                .bind(colaErrores())
                .to(exchangeGuias())
                .with(ROUTING_ERRORES);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public ApplicationRunner inicializarRabbitMQ(
            AmqpAdmin amqpAdmin,
            Queue colaGuias,
            Queue colaErrores,
            DirectExchange exchangeGuias,
            Binding bindingGuias,
            Binding bindingErrores) {

        return args -> {
            amqpAdmin.declareExchange(exchangeGuias);
            amqpAdmin.declareQueue(colaGuias);
            amqpAdmin.declareQueue(colaErrores);
            amqpAdmin.declareBinding(bindingGuias);
            amqpAdmin.declareBinding(bindingErrores);

            System.out.println("Colas RabbitMQ creadas correctamente");
        };
    }
}