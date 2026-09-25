package com.Projeto.pedido.service;

import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.domain.ItemCarrinho;
import com.Projeto.pedido.exceptions.RecursoNaoEncontradoException;
import com.Projeto.pedido.exceptions.RegraNegocioException;
import com.Projeto.pedido.repository.CarrinhoRepository;
import com.Projeto.pedido.client.CatalogoClient;
import com.Projeto.pedido.client.ProdutoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final CatalogoClient catalogoClient;

    public Carrinho criarCarrinho() {
        return carrinhoRepository.save(new Carrinho());
    }

    public Carrinho buscarPorId(Long id) {
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho não encontrado com ID: " + id));
    }

    public Carrinho adicionarItem(Long carrinhoId, Long produtoId, Integer quantidade) {
        Carrinho carrinho = buscarPorId(carrinhoId);

        ProdutoDTO produto = catalogoClient.listarTodos().stream()
                .filter(p -> p.getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado no catálogo"));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para o produto ID: " + produto.getId());
        }

        ItemCarrinho novoItem = new ItemCarrinho(null, produto.getId(), quantidade, produto.getPreco());
        carrinho.getItens().add(novoItem);

        return carrinhoRepository.save(carrinho);
    }
}