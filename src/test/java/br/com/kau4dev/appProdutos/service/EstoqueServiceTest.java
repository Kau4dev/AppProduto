package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.exception.EstoqueInsuficienteException;
import br.com.kau4dev.appProdutos.exception.EstoqueNaoEncontradoException;
import br.com.kau4dev.appProdutos.exception.IdProdutoNaoEncontradoException;
import br.com.kau4dev.appProdutos.mapper.EstoqueMapper;
import br.com.kau4dev.appProdutos.model.Estoque;
import br.com.kau4dev.appProdutos.model.Produto;
import br.com.kau4dev.appProdutos.repository.EstoqueRepository;
import br.com.kau4dev.appProdutos.repository.ProdutoRepository;
import br.com.kau4dev.appProdutos.repository.TransacaoEstoqueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private TransacaoEstoqueRepository transacaoRepository;
    @Mock
    private EstoqueMapper estoqueMapper;

    @InjectMocks
    private EstoqueService estoqueService;

    @Nested
    @DisplayName("Busca de estoque")
    class BuscarEstoque {
        @Test
        void buscarEstoqueDoProduto_deveRetornarQuandoEncontrado() {
            Long produtoId = 1L;
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setEstoqueMinimo(5);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));
            when(estoqueMapper.toViewDTO(estoque)).thenReturn(new EstoqueViewDTO(10L, 100, 5, produtoId, "Produto X"));

            var view = estoqueService.buscarEstoqueDoProduto(produtoId);

            assertEquals(10L, view.id());
            assertEquals(100, view.quantidade());
            assertEquals(5, view.estoqueMinimo());
        }

        @Test
        void buscarEstoqueDoProduto_deveLancarQuandoProdutoNaoExiste() {
            when(produtoRepository.existsById(99L)).thenReturn(false);
            assertThrows(IdProdutoNaoEncontradoException.class, () -> estoqueService.buscarEstoqueDoProduto(99L));
        }

        @Test
        void buscarEstoqueDoProduto_deveLancarQuandoEstoqueNaoEncontrado() {
            Long produtoId = 1L;
            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());
            assertThrows(EstoqueNaoEncontradoException.class, () -> estoqueService.buscarEstoqueDoProduto(produtoId));
        }
    }

    @Nested
    @DisplayName("Atualização de estoque")
    class AtualizarEstoque {
        @Test
        void atualizarEstoqueDoProduto_deveAtualizarQuandoValido() {
            Long produtoId = 1L;
            var dto = new EstoqueUpdateDTO(150, 2, "Ajuste inventário");
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setEstoqueMinimo(5);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));
            when(estoqueMapper.toViewDTO(estoque)).thenReturn(new EstoqueViewDTO(10L, 150, 2, produtoId, "Produto X"));

            var view = estoqueService.atualizarEstoqueDoProduto(produtoId, dto);

            assertEquals(150, view.quantidade());
            assertEquals(2, view.estoqueMinimo());
            verify(estoqueRepository).save(estoque);
            verify(transacaoRepository).save(any());
        }

        @Test
        void atualizarEstoqueDoProduto_deveRegistrarTransacaoQuandoMotivoFornecido() {
            Long produtoId = 1L;
            var dto = new EstoqueUpdateDTO(120, 3, "Recontagem de inventário");
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setEstoqueMinimo(5);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));
            when(estoqueMapper.toViewDTO(estoque)).thenReturn(new EstoqueViewDTO(10L, 120, 3, produtoId, "Produto X"));

            estoqueService.atualizarEstoqueDoProduto(produtoId, dto);

            assertEquals(120, estoque.getQuantidade());
            verify(transacaoRepository).save(argThat(transacao ->
                    transacao.getMotivo().equals("Recontagem de inventário") &&
                    transacao.getQuantidade() == 20
            ));
        }

        @Test
        void atualizarEstoqueDoProduto_deveUsarMotivosPadraaoQuandoMotivoNull() {
            Long produtoId = 1L;
            var dto = new EstoqueUpdateDTO(80, 3, null);
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setEstoqueMinimo(5);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));
            when(estoqueMapper.toViewDTO(estoque)).thenReturn(new EstoqueViewDTO(10L, 80, 3, produtoId, "Produto X"));

            estoqueService.atualizarEstoqueDoProduto(produtoId, dto);

            assertEquals(80, estoque.getQuantidade());
            verify(transacaoRepository).save(argThat(transacao ->
                    transacao.getMotivo().equals("Ajuste manual de estoque") &&
                    transacao.getQuantidade() == -20
            ));
        }

        @Test
        void atualizarEstoqueDoProduto_naoDeveRegistrarTransacaoQuandoDiferencaZero() {
            Long produtoId = 1L;
            var dto = new EstoqueUpdateDTO(100, 3, "Mesmo valor");
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setEstoqueMinimo(5);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));
            when(estoqueMapper.toViewDTO(estoque)).thenReturn(new EstoqueViewDTO(10L, 100, 3, produtoId, "Produto X"));

            estoqueService.atualizarEstoqueDoProduto(produtoId, dto);

            assertEquals(100, estoque.getQuantidade());
            assertEquals(3, estoque.getEstoqueMinimo());
            verify(estoqueRepository).save(estoque);
            verify(transacaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Adição de estoque")
    class AdicionarEstoque {
        @Test
        void adicionarEstoque_deveIncrementarQuantidadeQuandoValido() {
            Long produtoId = 1L;
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(10);
            estoque.setEstoqueMinimo(2);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            estoqueService.adicionarEstoque(produtoId, 5, "Compra fornecedor");

            assertEquals(15, estoque.getQuantidade());
            verify(estoqueRepository).save(estoque);
            verify(transacaoRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Remoção de estoque")
    class RemoverEstoque {
        @Test
        void removerEstoque_deveDecrementarQuantidadeQuandoDisponivel() {
            Long produtoId = 1L;
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(10);
            estoque.setEstoqueMinimo(2);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            estoqueService.removerEstoque(produtoId, 5, "Venda");

            assertEquals(5, estoque.getQuantidade());
            verify(estoqueRepository).save(estoque);
            verify(transacaoRepository).save(any());
        }

        @Test
        void removerEstoque_deveLancarQuandoEstoqueInsuficiente() {
            Long produtoId = 1L;
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(4);
            estoque.setEstoqueMinimo(2);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            assertThrows(EstoqueInsuficienteException.class, () -> estoqueService.removerEstoque(produtoId, 5, "Venda"));
            verify(estoqueRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Verificação de disponibilidade")
    class VerificarDisponibilidade {
        @Test
        void temEstoqueDisponivel_deveRetornarTrueQuandoSuficiente() {
            Long produtoId = 1L;
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(10);
            estoque.setEstoqueMinimo(2);

            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            assertTrue(estoqueService.temEstoqueDisponivel(produtoId, 5));
        }

        @Test
        void temEstoqueDisponivel_deveRetornarFalseQuandoInsuficiente() {
            Long produtoId = 1L;
            var produto = new Produto();
            produto.setId(produtoId);
            var estoque = new Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(10);
            estoque.setEstoqueMinimo(2);

            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            assertFalse(estoqueService.temEstoqueDisponivel(produtoId, 11));
        }
    }
}

