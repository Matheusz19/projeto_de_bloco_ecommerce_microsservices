package com.Projeto.catalogo.service;

import com.Projeto.catalogo.domain.Produto;
import com.Projeto.catalogo.dto.ResultadoEstoqueEvent;
import com.Projeto.catalogo.exceptions.RecursoNaoEncontradoException;
import com.Projeto.catalogo.exceptions.RegraNegocioException;
import com.Projeto.catalogo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RabbitTemplate rabbitTemplate;

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "estoque.baixar.queue")
    @org.springframework.transaction.annotation.Transactional
    public void baixarEstoque(com.Projeto.catalogo.dto.PedidoEvent evento) {
        try {
            for (com.Projeto.catalogo.dto.ItemPedidoDTO item : evento.getItens()) {
                Produto produto = produtoRepository.findById(item.getProdutoId())
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + item.getProdutoId()));

                if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                    throw new RegraNegocioException("Estoque insuficiente para o produto ID: " + produto.getId());
                }
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
                produtoRepository.save(produto);
            }

            rabbitTemplate.convertAndSend("pedidos.exchange", "estoque.resultado", new ResultadoEstoqueEvent(evento.getPedidoId(), true));
            System.out.println("Estoque baixado com sucesso para o pedido: " + evento.getPedidoId());

        } catch (Exception e) {
            rabbitTemplate.convertAndSend("pedidos.exchange", "estoque.resultado", new ResultadoEstoqueEvent(evento.getPedidoId(), false));
            System.out.println("Falha ao baixar estoque: " + e.getMessage());
            throw e;
        }
    }
}