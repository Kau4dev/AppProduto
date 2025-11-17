package br.com.kau4dev.appProdutos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "estoque_minimo", nullable = false)
    @NotNull(message = "O estoque mínimo não pode ser nulo")
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
    private Integer estoqueMinimo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToOne
    @JoinColumn(name = "produto_id", referencedColumnName = "id")
    @NotNull(message = "O produto não pode ser nulo")
    @JsonBackReference
    private Produto produto;

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL)
    private List<TransacaoEstoque> transacoes;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (estoqueMinimo == null) {
            estoqueMinimo = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}