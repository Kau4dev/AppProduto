package br.com.kau4dev.appProdutos.repository;

import br.com.kau4dev.appProdutos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

        boolean existsByNome(String nome);

}
