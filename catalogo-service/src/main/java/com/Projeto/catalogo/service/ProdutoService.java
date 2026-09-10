package com.Projeto.catalogo.service;

import com.Projeto.catalogo.domain.Produto;
import com.Projeto.catalogo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "estoque.baixar.queue")
    @org.springframework.transaction.annotation.Transactional
    public void baixarEstoque(com.Projeto.catalogo.dto.PedidoPagoEvent evento) {
        for (com.Projeto.catalogo.dto.ItemPedidoDTO item : evento.getItens()) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);
        }
        System.out.println("Estoque atualizado para o pedido: " + evento.getPedidoId());
    }
}