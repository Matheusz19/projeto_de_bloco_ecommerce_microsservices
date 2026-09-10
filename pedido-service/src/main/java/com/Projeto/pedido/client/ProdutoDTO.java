package com.Projeto.pedido.client;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoDTO {
    private Long id;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
}