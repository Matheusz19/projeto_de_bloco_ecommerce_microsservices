package com.Projeto.pedido.controller;

import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.domain.ItemCarrinho;
import com.Projeto.pedido.service.CarrinhoService;
import com.Projeto.pedido.repository.CarrinhoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarrinhoController.class)
class CarrinhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarrinhoService carrinhoService;

    @MockBean
    private CarrinhoRepository carrinhoRepository;

    @Test
    void deveCriarCarrinhoVazio() throws Exception {
        Carrinho carrinho = new Carrinho();
        carrinho.setId(1L);
        when(carrinhoService.criarCarrinho()).thenReturn(carrinho);

        mockMvc.perform(post("/api/carrinhos"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itens").isEmpty());
    }

    @Test
    void deveAdicionarItemAoCarrinho() throws Exception {
        Carrinho carrinhoAtualizado = new Carrinho();
        carrinhoAtualizado.setId(1L);
        carrinhoAtualizado.setItens(List.of(new ItemCarrinho(1L, 100L, 2, new BigDecimal("50.00"))));

        when(carrinhoService.adicionarItem(1L, 100L, 2)).thenReturn(carrinhoAtualizado);

        mockMvc.perform(post("/api/carrinhos/1/itens")
                        .param("produtoId", "100")
                        .param("quantidade", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].produtoId").value(100))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2));
    }
}