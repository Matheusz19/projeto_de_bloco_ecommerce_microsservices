package com.Projeto.pedido.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "catalogo-service")
public interface CatalogoClient {
    @GetMapping("/api/produtos")
    List<ProdutoDTO> listarTodos();
}