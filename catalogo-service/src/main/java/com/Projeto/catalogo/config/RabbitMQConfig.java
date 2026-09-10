package com.Projeto.catalogo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String FILA_ESTOQUE = "estoque.baixar.queue";
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String ROUTING_KEY_PAGO = "pedido.pago";

    @Bean
    public Queue filaEstoque() {
        return new Queue(FILA_ESTOQUE, true);
    }

    @Bean
    public TopicExchange exchangePedidos() {
        return new TopicExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    public Binding bindingEstoque() {
        return BindingBuilder.bind(filaEstoque()).to(exchangePedidos()).with(ROUTING_KEY_PAGO);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }
}