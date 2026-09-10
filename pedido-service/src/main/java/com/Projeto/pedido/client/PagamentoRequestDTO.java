package com.Projeto.pedido.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {
    private Long pedidoId;
    private BigDecimal valor;
}
