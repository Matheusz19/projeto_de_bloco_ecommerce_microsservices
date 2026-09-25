package pagamentoservice.service;

import pagamentoservice.domain.Pagamento;
import pagamentoservice.dto.*;
import pagamentoservice.repository.PagamentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoProcessadorServiceTest {

    @Mock
    private PagamentoRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PagamentoProcessadorService service;

    private PedidoEvent pedidoEvent;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        pedidoEvent = new PedidoEvent();
        pedidoEvent.setPedidoId(500L);

        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setPedidoId(500L);
        pagamento.setValor(BigDecimal.ZERO);
        pagamento.setStatus(Pagamento.StatusPagamento.APROVADO);
    }

    @Test
    void processarPagamento_DeveAprovarEEnviarMensagem() {
        when(repository.save(any(Pagamento.class))).thenReturn(pagamento);

        service.processarPagamento(pedidoEvent);

        verify(repository, times(1)).save(any(Pagamento.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("pagamento.processado"),
                any(Object.class)
        );
    }

    @Test
    void compensarPagamento_EstoqueFalhou_DeveEstornarPagamento() {
        ResultadoEstoqueEvent resultadoFalha = new ResultadoEstoqueEvent(500L, false);
        when(repository.findByPedidoId(500L)).thenReturn(Optional.of(pagamento));
        when(repository.save(any(Pagamento.class))).thenReturn(pagamento);

        service.compensarPagamento(resultadoFalha);

        assertEquals(Pagamento.StatusPagamento.RECUSADO, pagamento.getStatus());
        verify(repository, times(1)).save(pagamento);
    }

    @Test
    void compensarPagamento_EstoqueSucesso_NaoDeveFazerNada() {
        ResultadoEstoqueEvent resultadoSucesso = new ResultadoEstoqueEvent(500L, true);

        service.compensarPagamento(resultadoSucesso);

        verify(repository, never()).findByPedidoId(anyLong());
        verify(repository, never()).save(any(Pagamento.class));
    }
}
