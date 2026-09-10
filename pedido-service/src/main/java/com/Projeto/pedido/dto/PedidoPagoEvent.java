package com.Projeto.pedido.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoPagoEvent {
    private Long pedidoId;
    private List<ItemPedidoDTO> itens;
}