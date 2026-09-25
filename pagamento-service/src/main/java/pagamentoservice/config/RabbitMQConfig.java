package pagamentoservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";

    @Bean
    public TopicExchange exchangePedidos() {
        return new TopicExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    public Queue filaPedidoCriado() {
        return new Queue("pedido.criado.queue", true);
    }

    @Bean
    public Queue filaPagamentoCompensacao() {
        return new Queue("pagamento.compensacao.queue", true);
    }

    @Bean
    public Binding bindingPedidoCriado(Queue filaPedidoCriado, TopicExchange exchangePedidos) {
        return BindingBuilder.bind(filaPedidoCriado).to(exchangePedidos).with("pedido.criado");
    }

    @Bean
    public Binding bindingPagamentoCompensacao(Queue filaPagamentoCompensacao, TopicExchange exchangePedidos) {
        return BindingBuilder.bind(filaPagamentoCompensacao).to(exchangePedidos).with("estoque.resultado");
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonConverter());
        return template;
    }
}