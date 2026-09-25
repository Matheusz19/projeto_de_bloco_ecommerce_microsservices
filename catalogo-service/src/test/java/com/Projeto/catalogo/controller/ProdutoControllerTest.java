package com.Projeto.catalogo.controller;

import com.Projeto.catalogo.domain.Produto;
import com.Projeto.catalogo.service.ProdutoService;
import com.Projeto.catalogo.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private ProdutoRepository produtoRepository;

    @Test
    void deveListarProdutosComSucesso() throws Exception {
        Produto produto = new Produto(1L, "Teclado", "Mecânico", new BigDecimal("200.00"), 10);
        when(produtoService.listarTodos()).thenReturn(List.of(produto));

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Teclado"))
                .andExpect(jsonPath("$[0].quantidadeEstoque").value(10));
    }

    @Test
    void deveCadastrarProdutoComSucesso() throws Exception {
        Produto produtoSalvo = new Produto(1L, "Mouse", "Gamer", new BigDecimal("100.00"), 5);
        when(produtoService.salvar(any(Produto.class))).thenReturn(produtoSalvo);

        String jsonRequest = """
                {
                    "nome": "Mouse",
                    "preco": 100.00,
                    "quantidadeEstoque": 5
                }
                """;

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Mouse"));
    }
}