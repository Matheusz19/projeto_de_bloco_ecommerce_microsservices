package com.Projeto.catalogo.repository;

import com.Projeto.catalogo.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByQuantidadeEstoqueGreaterThan(Integer quantidade);

    List<Produto> findByPrecoBetween(BigDecimal min, BigDecimal max);
}