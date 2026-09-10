package com.Projeto.pedido.controller;

import com.Projeto.pedido.domain.Pedido;
import com.Projeto.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/checkout/{carrinhoId}")
    public ResponseEntity<Pedido> realizarCheckout(@PathVariable Long carrinhoId) {
        Pedido pedidoRealizado = pedidoService.realizarCheckout(carrinhoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoRealizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }
}