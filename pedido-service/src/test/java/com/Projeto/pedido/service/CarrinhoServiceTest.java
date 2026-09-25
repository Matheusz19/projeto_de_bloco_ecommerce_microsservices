package com.Projeto.pedido.service;

import com.Projeto.pedido.client.CatalogoClient;
import com.Projeto.pedido.client.ProdutoDTO;
import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.exceptions.RecursoNaoEncontradoException;
import com.Projeto.pedido.exceptions.RegraNegocioException;
import com.Projeto.pedido.repository.CarrinhoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private CarrinhoService carrinhoService;

    private Carrinho carrinho;
    private ProdutoDTO produtoComEstoque;
    private ProdutoDTO produtoSemEstoque;

    @BeforeEach
    void setUp() {
        carrinho = new Carrinho();
        carrinho.setId(1L);
        carrinho.setItens(new ArrayList<>());

        produtoComEstoque = new ProdutoDTO();
        produtoComEstoque.setId(100L);
        produtoComEstoque.setPreco(new BigDecimal("50.00"));
        produtoComEstoque.setQuantidadeEstoque(10);

        produtoSemEstoque = new ProdutoDTO();
        produtoSemEstoque.setId(101L);
        produtoSemEstoque.setPreco(new BigDecimal("30.00"));
        produtoSemEstoque.setQuantidadeEstoque(0);
    }

    @Test
    void buscarPorId_CarrinhoExiste_DeveRetornarCarrinho() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));

        Carrinho resultado = carrinhoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_CarrinhoNaoExiste_DeveLancarException() {
        when(carrinhoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> carrinhoService.buscarPorId(99L));
    }

    @Test
    void adicionarItem_ProdutoComEstoque_DeveAdicionarComSucesso() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(catalogoClient.listarTodos()).thenReturn(List.of(produtoComEstoque));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        Carrinho resultado = carrinhoService.adicionarItem(1L, 100L, 2);

        assertNotNull(resultado);
        assertEquals(1, resultado.getItens().size());
        assertEquals(100L, resultado.getItens().get(0).getProdutoId());
        verify(carrinhoRepository, times(1)).save(any(Carrinho.class));
    }

    @Test
    void adicionarItem_EstoqueInsuficiente_DeveLancarException() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(catalogoClient.listarTodos()).thenReturn(List.of(produtoSemEstoque));

        assertThrows(RegraNegocioException.class, () -> carrinhoService.adicionarItem(1L, 101L, 1));
        verify(carrinhoRepository, never()).save(any(Carrinho.class));
    }

    @Test
    void adicionarItem_ProdutoNaoEncontrado_DeveLancarException() {
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
        when(catalogoClient.listarTodos()).thenReturn(List.of(produtoComEstoque));

        assertThrows(RecursoNaoEncontradoException.class, () -> carrinhoService.adicionarItem(1L, 999L, 1));
    }
}