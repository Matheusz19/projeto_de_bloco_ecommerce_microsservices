package pagamentoservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pagamentoservice.domain.Pagamento;
import pagamentoservice.repository.PagamentoRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoRepository repository;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pagamento> buscarPorPedido(@PathVariable Long pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}