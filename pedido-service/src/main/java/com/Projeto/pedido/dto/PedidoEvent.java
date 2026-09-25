package com.Projeto.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEvent {
    private Long pedidoId;
    private List<ItemPedidoDTO> itens;
}