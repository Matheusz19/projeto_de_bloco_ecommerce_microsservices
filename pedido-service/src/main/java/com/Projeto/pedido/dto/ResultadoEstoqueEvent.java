package com.Projeto.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEstoqueEvent {
    private Long pedidoId;
    private boolean sucesso;
}