package com.Projeto.catalogo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
@Audited
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    private String descricao;

    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @PositiveOrZero(message = "O estoque não pode ser negativo")
    private Integer quantidadeEstoque;
}