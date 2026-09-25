package pagamentoservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pagamentoservice.domain.Pagamento;
import pagamentoservice.dto.*;
import pagamentoservice.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagamentoProcessadorService {

    private final PagamentoRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "pedido.criado.queue")
    public void processarPagamento(PedidoEvent evento) {
        Pagamento pagamento = new Pagamento();
        pagamento.setPedidoId(evento.getPedidoId());
        pagamento.setValor(BigDecimal.ZERO);
        pagamento.setStatus(Pagamento.StatusPagamento.APROVADO);
        pagamento.setDataProcessamento(LocalDateTime.now());

        repository.save(pagamento);

        rabbitTemplate.convertAndSend("pedidos.exchange", "pagamento.processado", evento);
    }

    @RabbitListener(queues = "pagamento.compensacao.queue")
    public void compensarPagamento(ResultadoEstoqueEvent resultado) {
        if (!resultado.isSucesso()) {
            repository.findByPedidoId(resultado.getPedidoId()).ifPresent(pagamento -> {
                pagamento.setStatus(Pagamento.StatusPagamento.RECUSADO);
                repository.save(pagamento);
                System.out.println("Pagamento estornado por falta de estoque. Pedido: " + resultado.getPedidoId());
            });
        }
    }
}