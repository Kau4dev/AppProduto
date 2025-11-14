package br.com.kau4dev.appProdutos.dto.estoqueDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EstoqueCreateDTO(
        @NotNull(message = "A quantidade não pode ser nula")
        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantidade,

        @NotNull(message = "A observação não pode ser nula")
        Boolean observacao
) {}
