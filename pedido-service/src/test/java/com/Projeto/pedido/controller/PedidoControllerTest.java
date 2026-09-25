package com.Projeto.pedido.controller;

import com.Projeto.pedido.domain.Pedido;
import com.Projeto.pedido.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Test
    void deveRealizarCheckoutComSucesso() throws Exception {
        Long carrinhoId = 10L;
        Pedido pedidoMock = new Pedido();

        when(pedidoService.realizarCheckout(carrinhoId)).thenReturn(pedidoMock);

        mockMvc.perform(post("/api/pedidos/checkout/{carrinhoId}", carrinhoId))
                .andExpect(status().isCreated());
    }

    @Test
    void deveBuscarPedidoPorIdComSucesso() throws Exception {
        Long pedidoId = 1L;
        Pedido pedidoMock = new Pedido();

        when(pedidoService.buscarPorId(pedidoId)).thenReturn(pedidoMock);

        mockMvc.perform(get("/api/pedidos/{id}", pedidoId))
                .andExpect(status().isOk());
    }
}