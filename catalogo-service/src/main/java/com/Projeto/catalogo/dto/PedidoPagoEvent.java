package com.Projeto.catalogo.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoPagoEvent {
    private Long pedidoId;
    private List<ItemPedidoDTO> itens;
}