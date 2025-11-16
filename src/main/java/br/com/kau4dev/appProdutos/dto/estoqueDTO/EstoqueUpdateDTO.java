package br.com.kau4dev.appProdutos.dto.estoqueDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EstoqueUpdateDTO(
        @NotNull(message = "A quantidade não pode ser nula")
        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantidade,

        @NotNull(message = "O estoque mínimo não pode ser nulo")
        @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
        Integer estoqueMinimo,

        String motivo
) {}
