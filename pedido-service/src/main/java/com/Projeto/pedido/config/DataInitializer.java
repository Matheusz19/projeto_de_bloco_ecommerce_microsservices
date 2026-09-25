package com.Projeto.pedido.config;

import com.Projeto.pedido.domain.Carrinho;
import com.Projeto.pedido.repository.CarrinhoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CarrinhoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Carrinho carrinho = new Carrinho();
                repository.save(carrinho);
                System.out.println("Carrinho inicial (ID: 1) criado automaticamente!");
            }
        };
    }
}