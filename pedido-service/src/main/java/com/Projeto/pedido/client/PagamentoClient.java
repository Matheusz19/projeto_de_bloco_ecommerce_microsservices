package com.Projeto.pedido.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pagamento-service", url = "http://localhost:8081/api/pagamentos")
public interface PagamentoClient {

    @PostMapping("/processar")
    Object processarPagamento(@RequestBody PagamentoRequestDTO request);
}