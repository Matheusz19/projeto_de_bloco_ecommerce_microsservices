package pagamentoservice.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pagamentoservice.domain.Pagamento;
import pagamentoservice.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoRepository repository;

    @Test
    @DisplayName("Deve buscar o pagamento pelo ID do pedido com sucesso")
    void deveBuscarPagamentoPorPedido() throws Exception {
        Pagamento pagamentoMock = new Pagamento();
        pagamentoMock.setId(1L);
        pagamentoMock.setPedidoId(99L);
        pagamentoMock.setValor(new BigDecimal("150.00"));
        pagamentoMock.setStatus(Pagamento.StatusPagamento.APROVADO);

        when(repository.findByPedidoId(99L)).thenReturn(Optional.of(pagamentoMock));

        mockMvc.perform(get("/api/pagamentos/pedido/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.pedidoId").value(99));
    }
}