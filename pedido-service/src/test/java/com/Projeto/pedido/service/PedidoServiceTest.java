package com.Projeto.pedido.service;

import com.Projeto.pedido.domain.*;
import com.Projeto.pedido.exceptions.RegraNegocioException;
import com.Projeto.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CarrinhoService carrinhoService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    private Carrinho carrinhoVazio;
    private Carrinho carrinhoComItens;

    @BeforeEach
    void setUp() {
        carrinhoVazio = new Carrinho();
        carrinhoVazio.setId(1L);

        carrinhoComItens = new Carrinho();
        carrinhoComItens.setId(2L);
        carrinhoComItens.setItens(List.of(
                new ItemCarrinho(1L, 100L, 2, new BigDecimal("50.00"))
        ));
    }

    @Test
    void realizarCheckout_CarrinhoVazio_DeveLancarException() {
        when(carrinhoService.buscarPorId(1L)).thenReturn(carrinhoVazio);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pedidoService.realizarCheckout(1L);
        });

        assertEquals("Não é possível fechar um pedido com o carrinho vazio.", exception.getMessage());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void realizarCheckout_CarrinhoComItens_DeveCriarPedido() {
        when(carrinhoService.buscarPorId(2L)).thenReturn(carrinhoComItens);

        Pedido pedidoSalvoMock = new Pedido();
        pedidoSalvoMock.setId(10L);
        pedidoSalvoMock.setStatus(StatusPedido.PENDENTE);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvoMock);

        Pedido resultado = pedidoService.realizarCheckout(2L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(StatusPedido.PENDENTE, resultado.getStatus());

        verify(rabbitTemplate, times(1)).convertAndSend(eq("pedidos.exchange"), eq("pedido.criado"), any(Object.class));    }
}
