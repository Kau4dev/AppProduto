package br.com.kau4dev.appProdutos.repository;

import br.com.kau4dev.appProdutos.model.TransacaoEstoque;
import br.com.kau4dev.appProdutos.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoEstoqueRepository extends JpaRepository<TransacaoEstoque, Long> {

    List<TransacaoEstoque> findByEstoqueId(Long estoqueId);

    List<TransacaoEstoque> findByTipo(TipoTransacao tipo);
}
