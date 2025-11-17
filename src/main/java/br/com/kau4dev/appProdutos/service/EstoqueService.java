package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.exception.EstoqueInsuficienteException;
import br.com.kau4dev.appProdutos.exception.EstoqueNaoEncontradoException;
import br.com.kau4dev.appProdutos.exception.IdProdutoNaoEncontradoException;
import br.com.kau4dev.appProdutos.mapper.EstoqueMapper;
import br.com.kau4dev.appProdutos.model.Estoque;
import br.com.kau4dev.appProdutos.model.TransacaoEstoque;
import br.com.kau4dev.appProdutos.model.enums.TipoTransacao;
import br.com.kau4dev.appProdutos.repository.EstoqueRepository;
import br.com.kau4dev.appProdutos.repository.ProdutoRepository;
import br.com.kau4dev.appProdutos.repository.TransacaoEstoqueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueMapper estoqueMapper;
    private final TransacaoEstoqueRepository transacaoRepository;

    public EstoqueViewDTO buscarEstoqueDoProduto(Long produtoId) {
        validarProdutoExiste(produtoId);
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNaoEncontradoException("Estoque não encontrado para o produto com ID: " + produtoId));
        return estoqueMapper.toViewDTO(estoque);
    }

    @Transactional
    public EstoqueViewDTO atualizarEstoqueDoProduto(Long produtoId, EstoqueUpdateDTO estoqueUpdateDTO) {
        validarProdutoExiste(produtoId);
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNaoEncontradoException("Estoque não encontrado para o produto com ID: " + produtoId));

        int quantidadeAnterior = estoque.getQuantidade();
        int diferenca = estoqueUpdateDTO.quantidade() - quantidadeAnterior;

        estoque.setQuantidade(estoqueUpdateDTO.quantidade());
        estoque.setEstoqueMinimo(estoqueUpdateDTO.estoqueMinimo());
        estoqueRepository.save(estoque);

        if (diferenca != 0) {
            registrarTransacao(estoque, diferenca, TipoTransacao.AJUSTE,
                estoqueUpdateDTO.motivo() != null ? estoqueUpdateDTO.motivo() : "Ajuste manual de estoque");
        }

        return estoqueMapper.toViewDTO(estoque);
    }

    @Transactional
    public void adicionarEstoque(Long produtoId, int quantidade, String motivo) {
        validarProdutoExiste(produtoId);
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNaoEncontradoException("Estoque não encontrado para o produto com ID: " + produtoId));

        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        estoqueRepository.save(estoque);

        registrarTransacao(estoque, quantidade, TipoTransacao.ENTRADA, motivo);
    }

    @Transactional
    public void removerEstoque(Long produtoId, int quantidade, String motivo) {
        validarProdutoExiste(produtoId);

        if (!temEstoqueDisponivel(produtoId, quantidade)) {
            Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                    .orElseThrow(() -> new EstoqueNaoEncontradoException("Estoque não encontrado para o produto com ID: " + produtoId));
            throw new EstoqueInsuficienteException("Estoque insuficiente. Disponível: " + estoque.getQuantidade() + ", Solicitado: " + quantidade);
        }

        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNaoEncontradoException("Estoque não encontrado para o produto com ID: " + produtoId));

        estoque.setQuantidade(estoque.getQuantidade() - quantidade);
        estoqueRepository.save(estoque);

        registrarTransacao(estoque, -quantidade, TipoTransacao.SAIDA, motivo);
    }

    public boolean temEstoqueDisponivel(Long produtoId, int quantidade) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNaoEncontradoException("Estoque não encontrado para o produto com ID: " + produtoId));
        return estoque.getQuantidade() >= quantidade;
    }

    private void validarProdutoExiste(Long produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new IdProdutoNaoEncontradoException("Produto não encontrado com ID: " + produtoId);
        }
    }

    private void registrarTransacao(Estoque estoque, int quantidade, TipoTransacao tipo, String motivo) {
        TransacaoEstoque transacao = new TransacaoEstoque();
        transacao.setEstoque(estoque);
        transacao.setQuantidade(quantidade);
        transacao.setTipo(tipo);
        transacao.setMotivo(motivo);
        transacao.setDataCriacao(LocalDateTime.now());
        transacaoRepository.save(transacao);
    }
}
