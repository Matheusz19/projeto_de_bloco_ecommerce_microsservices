package com.Projeto.pedido.controller;

import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.service.CarrinhoService;
import com.Projeto.pedido.repository.CarrinhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final CarrinhoRepository carrinhoRepository;

    @PostMapping
    public ResponseEntity<Carrinho> criarCarrinho() {
        return ResponseEntity.status(HttpStatus.CREATED).body(carrinhoService.criarCarrinho());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarCarrinho(@PathVariable Long id) {
        return ResponseEntity.ok(carrinhoService.buscarPorId(id));
    }

    @PostMapping("/{carrinhoId}/itens")
    public ResponseEntity<Carrinho> adicionarItem(
            @PathVariable Long carrinhoId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {

        Carrinho carrinhoAtualizado = carrinhoService.adicionarItem(carrinhoId, produtoId, quantidade);
        return ResponseEntity.ok(carrinhoAtualizado);
    }

    @DeleteMapping("/{carrinhoId}/itens/{produtoId}")
    public ResponseEntity<Carrinho> removerItemDoCarrinho(@PathVariable Long carrinhoId, @PathVariable Long produtoId) {
        return carrinhoRepository.findById(carrinhoId)
                .map(carrinho -> {
                    carrinho.getItens().removeIf(item -> item.getProdutoId().equals(produtoId));
                    Carrinho carrinhoAtualizado = carrinhoRepository.save(carrinho);
                    return ResponseEntity.ok(carrinhoAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{carrinhoId}/itens/{produtoId}/diminuir")
    public ResponseEntity<Carrinho> diminuirItemDoCarrinho(@PathVariable Long carrinhoId, @PathVariable Long produtoId) {
        return carrinhoRepository.findById(carrinhoId)
                .map(carrinho -> {
                    carrinho.getItens().stream()
                            .filter(item -> item.getProdutoId().equals(produtoId))
                            .findFirst()
                            .ifPresent(item -> {
                                if (item.getQuantidade() > 1) {
                                    item.setQuantidade(item.getQuantidade() - 1);
                                } else {
                                    carrinho.getItens().remove(item);
                                }
                            });

                    Carrinho carrinhoAtualizado = carrinhoRepository.save(carrinho);
                    return ResponseEntity.ok(carrinhoAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}