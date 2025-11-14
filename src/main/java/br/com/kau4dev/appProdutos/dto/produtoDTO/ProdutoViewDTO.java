package br.com.kau4dev.appProdutos.dto.produtoDTO;

import java.math.BigDecimal;

public record ProdutoViewDTO(Long id, String nome, String codigoBarras, BigDecimal preco) {
}
