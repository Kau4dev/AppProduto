package br.com.kau4dev.appProdutos.dto.CategoriaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaUpdateDTO (

        @NotNull(message = "O nome da categoria não pode ser nulo")
        @NotBlank(message = "O nome deve obrigatório")
        String nome
){}



