package com.Projeto.pedido.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrinhos")
@Data
@NoArgsConstructor
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrinho_id")
    private List<ItemCarrinho> itens = new ArrayList<>();

    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
