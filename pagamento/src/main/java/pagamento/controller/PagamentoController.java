package pagamento.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pagamento.domain.Pagamento;
import pagamento.repository.PagamentoRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoRepository repository;

    @PostMapping("/processar")
    public ResponseEntity<Pagamento> processarPagamento(@RequestBody Pagamento pagamento) {
        pagamento.setStatus(Pagamento.StatusPagamento.APROVADO);
        pagamento.setDataProcessamento(LocalDateTime.now());

        Pagamento salvo = repository.save(pagamento);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pagamento> buscarPorPedido(@PathVariable Long pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}