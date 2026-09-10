package com.Projeto.pedido.service;

import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.client.PagamentoClient;
import com.Projeto.pedido.client.PagamentoRequestDTO;
import com.Projeto.pedido.domain.ItemPedido;
import com.Projeto.pedido.domain.Pedido;
import com.Projeto.pedido.domain.StatusPedido;
import com.Projeto.pedido.repository.PedidoRepository;
import com.Projeto.pedido.dto.PedidoPagoEvent;
import com.Projeto.pedido.dto.ItemPedidoDTO;
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
    private final PagamentoClient pagamentoClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Pedido realizarCheckout(Long carrinhoId) {
        Carrinho carrinho = carrinhoService.buscarPorId(carrinhoId);

        if (carrinho.getItens().isEmpty()) {
            throw new RuntimeException("Não é possível fechar pedido com carrinho vazio.");
        }

        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTotal(carrinho.calcularTotal());

        List<ItemPedido> itensPedido = carrinho.getItens().stream()
                .map(itemCart -> new ItemPedido(
                        null,
                        itemCart.getProdutoId(),
                        itemCart.getQuantidade(),
                        itemCart.getPrecoUnitario()))
                .collect(Collectors.toList());

        pedido.getItens().addAll(itensPedido);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        PagamentoRequestDTO pagamentoRequest = new PagamentoRequestDTO(pedidoSalvo.getId(), pedidoSalvo.getTotal());
        pagamentoClient.processarPagamento(pagamentoRequest);

        pedidoSalvo.setStatus(StatusPedido.PAGO);
        Pedido pedidoFinal = pedidoRepository.save(pedidoSalvo);

        PedidoPagoEvent evento = new PedidoPagoEvent();
        evento.setPedidoId(pedidoFinal.getId());

        List<ItemPedidoDTO> dtoItens = pedidoFinal.getItens().stream()
                .map(item -> new ItemPedidoDTO(item.getProdutoId(), item.getQuantidade()))
                .collect(Collectors.toList());
        evento.setItens(dtoItens);

        rabbitTemplate.convertAndSend("pedidos.exchange", "pedido.pago", evento);

        return pedidoFinal;
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }
}