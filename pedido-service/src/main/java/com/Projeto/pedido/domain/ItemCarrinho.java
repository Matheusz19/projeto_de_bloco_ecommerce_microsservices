package com.Projeto.pedido.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_carrinho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long produtoId;

    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @Positive
    private BigDecimal precoUnitario;

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }
}
