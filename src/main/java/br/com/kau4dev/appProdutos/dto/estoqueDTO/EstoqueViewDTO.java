package br.com.kau4dev.appProdutos.dto.estoqueDTO;

public record EstoqueViewDTO(
        Long id,
        Integer quantidade,
        Integer estoqueMinimo,
        Long produtoId,
        String nomeProduto
) {}
