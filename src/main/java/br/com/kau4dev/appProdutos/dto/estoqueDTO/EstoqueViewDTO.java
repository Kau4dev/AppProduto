package br.com.kau4dev.appProdutos.dto.estoqueDTO;

public record EstoqueViewDTO(
        Long id,
        Integer quantidade,
        Boolean observacao,
        Long produtoId
) {}
