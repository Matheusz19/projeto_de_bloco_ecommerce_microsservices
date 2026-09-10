package com.Projeto.pedido.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Data
@Audited
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}