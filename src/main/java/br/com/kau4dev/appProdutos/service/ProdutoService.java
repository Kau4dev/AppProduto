package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoCreateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoUpdateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoViewDTO;
import br.com.kau4dev.appProdutos.exception.CategoriaNaoEncontradaException;
import br.com.kau4dev.appProdutos.exception.IdProdutoNaoEncontradoException;
import br.com.kau4dev.appProdutos.exception.PrecoDeveSerMaiorQueZeroException;
import br.com.kau4dev.appProdutos.exception.ProdutoJaExisteException;
import br.com.kau4dev.appProdutos.mapper.ProdutoMapper;
import br.com.kau4dev.appProdutos.model.Categoria;
import br.com.kau4dev.appProdutos.model.Estoque;
import br.com.kau4dev.appProdutos.model.Produto;
import br.com.kau4dev.appProdutos.repository.CategoriaRepository;
import br.com.kau4dev.appProdutos.repository.EstoqueRepository;
import br.com.kau4dev.appProdutos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtomapper;
    private final CategoriaRepository categoriaRepository;
    private final EstoqueRepository estoqueRepository;

    public ProdutoViewDTO criarProduto(ProdutoCreateDTO produtoCreateDTO) throws ProdutoJaExisteException {
        if (produtoCreateDTO.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PrecoDeveSerMaiorQueZeroException("Preço deve ser maior que zero");
        }

        Categoria categoria = categoriaRepository.findById(produtoCreateDTO.idCategoria())
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada com o id: " + produtoCreateDTO.idCategoria()));

        Produto produto = produtomapper.toEntity(produtoCreateDTO);

        if(produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
            throw new ProdutoJaExisteException("Produto com o código de barras " + produto.getCodigoBarras() + " já existe.");
        }

        produto.setIdCategoria(categoria);
        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        Estoque estoque = new Estoque();
        estoque.setProduto(produtoSalvo);
        estoque.setQuantidade(0);
        estoque.setEstoqueMinimo(0);
        estoqueRepository.save(estoque);

        return produtomapper.toViewDTO(produtoSalvo);
    }

    public List<ProdutoViewDTO> listarProdutos() {
        List<Produto> usuarios = produtoRepository.findAll();
        return usuarios.stream()
                .map(produtomapper::toViewDTO)
                .toList();
    }

    public ProdutoViewDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(
                () -> new IdProdutoNaoEncontradoException("Produto não encontrado com o id: " + id)
        );
        return produtomapper.toViewDTO(produto);
    }

    public ProdutoViewDTO atualizarProduto(Long id, ProdutoUpdateDTO produtoUpdateDTO) {
        Produto produto = produtoRepository.findById(id).orElseThrow(
                () -> new IdProdutoNaoEncontradoException("Produto não encontrado com o id: " + id)
        );
        if (produtoUpdateDTO.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PrecoDeveSerMaiorQueZeroException("Preço deve ser maior que zero");
        }
        produtomapper.updateEntityFromDTO(produtoUpdateDTO, produto);

        Produto produtoAtualizado = produtoRepository.saveAndFlush(produto);
        return produtomapper.toViewDTO(produtoAtualizado);
    }

    public void deletarProduto(Long id) {
        produtoRepository.findById(id).orElseThrow(
                () -> new IdProdutoNaoEncontradoException("Produto não encontrado com o id: " + id)
        );
        produtoRepository.deleteById(id);
    }
}


