package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoCreateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoUpdateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoViewDTO;
import br.com.kau4dev.appProdutos.exception.IdProdutoNaoEncontradoException;
import br.com.kau4dev.appProdutos.exception.PrecoDeveSerMaiorQueZeroException;
import br.com.kau4dev.appProdutos.exception.ProdutoJaExisteException;
import br.com.kau4dev.appProdutos.mapper.EstoqueMapper;
import br.com.kau4dev.appProdutos.mapper.ProdutoMapper;
import br.com.kau4dev.appProdutos.model.Estoque;
import br.com.kau4dev.appProdutos.model.Produto;
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
    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    public ProdutoViewDTO criarProduto(ProdutoCreateDTO produtoCreateDTO) throws ProdutoJaExisteException {
        Produto produto = produtomapper.toEntity(produtoCreateDTO);
        if(produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
            throw new ProdutoJaExisteException("Produto com o código de barras " + produto.getCodigoBarras() + " já existe.");
        }
        if (produtoCreateDTO.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PrecoDeveSerMaiorQueZeroException("Preço deve ser maior que zero");
        }
        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);
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

    public EstoqueViewDTO buscarEstoqueDoProduto(Long produtoId) {
        validarProdutoExiste(produtoId);
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado para o produto com ID: " + produtoId));
        return estoqueMapper.toViewDTO(estoque);
    }

    public EstoqueViewDTO criarEstoqueDoProduto(Long produtoId, EstoqueCreateDTO dto) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IdProdutoNaoEncontradoException("Produto não encontrado com ID: " + produtoId));

        if (estoqueRepository.existsByProdutoId(produtoId)) {
            throw new RuntimeException("Este produto já possui estoque cadastrado");
        }

        Estoque estoque = estoqueMapper.toEntity(dto);
        estoque.setProduto(produto);

        Estoque salvo = estoqueRepository.saveAndFlush(estoque);
        return estoqueMapper.toViewDTO(salvo);
    }
    public EstoqueViewDTO atualizarEstoqueDoProduto(Long produtoId, EstoqueUpdateDTO dto) {
        validarProdutoExiste(produtoId);

        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado para o produto com ID: " + produtoId));

        estoqueMapper.updateEntityFromDTO(dto, estoque);
        Estoque atualizado = estoqueRepository.saveAndFlush(estoque);
        return estoqueMapper.toViewDTO(atualizado);
    }

    private void validarProdutoExiste(Long produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new IdProdutoNaoEncontradoException("Produto não encontrado com ID: " + produtoId);
        }
    }
}


