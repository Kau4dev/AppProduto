package br.com.kau4dev.appProdutos.dto.produtoDTO;

import java.math.BigDecimal;

public record ProdutoViewDTO(Long id, String nome, String sku, String codigoBarras, String descricao, BigDecimal preco, BigDecimal precoCusto, Long categoriaId) {
}
