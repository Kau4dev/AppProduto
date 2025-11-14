package br.com.kau4dev.appProdutos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotNull(message = "A quantidade não pode ser nula")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidade;

    @OneToOne
    @JoinColumn(name = "produto_id", referencedColumnName = "id")
    @NotNull(message = "O produto não pode ser nulo")
    private Produto produto;

    @Column(nullable = false)
    @NotNull(message = "A observação não pode ser nula")
    @JsonBackReference
    //@JsonIgnore
    private Boolean observacao;
}