package com.Projeto.pedido.service;

import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.domain.ItemPedido;
import com.Projeto.pedido.domain.Pedido;
import com.Projeto.pedido.domain.StatusPedido;
import com.Projeto.pedido.exceptions.RecursoNaoEncontradoException;
import com.Projeto.pedido.exceptions.RegraNegocioException;
import com.Projeto.pedido.repository.PedidoRepository;
import com.Projeto.pedido.dto.PedidoEvent;
import com.Projeto.pedido.dto.ItemPedidoDTO;
import com.Projeto.pedido.dto.ResultadoEstoqueEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Pedido realizarCheckout(Long carrinhoId) {
        Carrinho carrinho = carrinhoService.buscarPorId(carrinhoId);
        if (carrinho.getItens().isEmpty()) {
            throw new RegraNegocioException("Não é possível fechar um pedido com o carrinho vazio.");
        }

        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTotal(carrinho.calcularTotal());

        List<ItemPedido> itensPedido = carrinho.getItens().stream()
                .map(itemCart -> new ItemPedido(null, itemCart.getProdutoId(), itemCart.getQuantidade(), itemCart.getPrecoUnitario()))
                .collect(Collectors.toList());
        pedido.getItens().addAll(itensPedido);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        PedidoEvent evento = new PedidoEvent();
        evento.setPedidoId(pedidoSalvo.getId());
        evento.setItens(pedidoSalvo.getItens().stream()
                .map(item -> new ItemPedidoDTO(item.getProdutoId(), item.getQuantidade()))
                .collect(Collectors.toList()));

        rabbitTemplate.convertAndSend("pedidos.exchange", "pedido.criado", evento);
        return pedidoSalvo;
    }

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "pedido.resultado.queue")
    @Transactional
    public void finalizarPedido(ResultadoEstoqueEvent resultado) {
        Pedido pedido = buscarPorId(resultado.getPedidoId());

        if (resultado.isSucesso()) {
            pedido.setStatus(StatusPedido.PAGO);
        } else {
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com ID: " + id));
    }
}