package com.Projeto.catalogo.service;

import com.Projeto.catalogo.domain.Produto;
import com.Projeto.catalogo.dto.*;
import com.Projeto.catalogo.exceptions.*;
import com.Projeto.catalogo.repository.ProdutoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private PedidoEvent pedidoEventSucesso;
    private PedidoEvent pedidoEventFalha;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Teclado Mecânico");
        produto.setPreco(new BigDecimal("250.00"));
        produto.setQuantidadeEstoque(10);

        ItemPedidoDTO itemSucesso = new ItemPedidoDTO();
        itemSucesso.setProdutoId(1L);
        itemSucesso.setQuantidade(2);

        pedidoEventSucesso = new PedidoEvent();
        pedidoEventSucesso.setPedidoId(500L);
        pedidoEventSucesso.setItens(List.of(itemSucesso));

        ItemPedidoDTO itemFalha = new ItemPedidoDTO();
        itemFalha.setProdutoId(1L);
        itemFalha.setQuantidade(15);

        pedidoEventFalha = new PedidoEvent();
        pedidoEventFalha.setPedidoId(501L);
        pedidoEventFalha.setItens(List.of(itemFalha));
    }

    @Test
    void baixarEstoque_ComSucesso_DeveAtualizarEstoqueEEnviarEventoTrue() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        produtoService.baixarEstoque(pedidoEventSucesso);

        assertEquals(8, produto.getQuantidadeEstoque());
        verify(produtoRepository, times(1)).save(produto);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("estoque.resultado"),
                any(Object.class)
        );
    }

    @Test
    void baixarEstoque_EstoqueInsuficiente_DeveLancarExceptionEEnviarEventoFalse() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(RegraNegocioException.class, () -> {
            produtoService.baixarEstoque(pedidoEventFalha);
        });

        verify(produtoRepository, never()).save(any(Produto.class));

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("estoque.resultado"),
                any(Object.class)
        );
    }

    @Test
    void baixarEstoque_ProdutoNaoEncontrado_DeveLancarExceptionEEnviarEventoFalse() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            produtoService.baixarEstoque(pedidoEventSucesso);
        });

        verify(produtoRepository, never()).save(any(Produto.class));

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedidos.exchange"),
                eq("estoque.resultado"),
                any(Object.class)
        );
    }
}