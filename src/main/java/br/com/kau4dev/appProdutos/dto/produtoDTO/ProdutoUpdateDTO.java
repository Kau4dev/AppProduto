package br.com.kau4dev.appProdutos.dto.produtoDTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoUpdateDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "Código de barras é obrigatório")
        @Pattern(regexp = "\\d{13}", message = "Código de barras deve ter 13 dígitos")
        String codigoBarras,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "Preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
        BigDecimal preco) {
}
