package pagamento.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pagamento.domain.Pagamento;
import pagamento.repository.PagamentoRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoRepository repository;

    @Test
    @DisplayName("Deve processar o pagamento e retornar status APROVADO")
    void deveProcessarPagamento() throws Exception {
        Pagamento pagamentoMock = new Pagamento();
        pagamentoMock.setId(1L);
        pagamentoMock.setPedidoId(99L);
        pagamentoMock.setStatus(Pagamento.StatusPagamento.APROVADO);

        when(repository.save(any(Pagamento.class))).thenReturn(pagamentoMock);

        String jsonRequest = """
                {
                    "pedidoId": 99,
                    "valor": 150.00
                }
                """;

        mockMvc.perform(post("/api/pagamentos/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.id").value(1));
    }
}