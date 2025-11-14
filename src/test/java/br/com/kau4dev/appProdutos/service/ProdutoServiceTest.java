package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoCreateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoUpdateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoViewDTO;
import br.com.kau4dev.appProdutos.exception.IdProdutoNaoEncontradoException;
import br.com.kau4dev.appProdutos.exception.PrecoDeveSerMaiorQueZeroException;
import br.com.kau4dev.appProdutos.exception.ProdutoJaExisteException;
import br.com.kau4dev.appProdutos.mapper.EstoqueMapper;
import br.com.kau4dev.appProdutos.mapper.ProdutoMapper;
import br.com.kau4dev.appProdutos.model.Produto;
import br.com.kau4dev.appProdutos.repository.EstoqueRepository;
import br.com.kau4dev.appProdutos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ProdutoMapper produtomapper;

    @Mock
    private EstoqueMapper estoqueMapper;

    @InjectMocks
    private ProdutoService produtoService;


    @Nested
    @DisplayName("Testes de Criação de Produto")
    class CriarProduto {
        @Test
        void criarProduto_deveSalvarQuandoValido() throws ProdutoJaExisteException {
            var dto = new ProdutoCreateDTO(null, "Caneta", "1234567890123", new BigDecimal("10.00"));

            var entidade = new Produto();
            entidade.setNome("Caneta");
            entidade.setCodigoBarras("1234567890123");
            entidade.setPreco(new BigDecimal("10.00"));

            var salvo = new Produto();
            salvo.setId(1L);
            salvo.setNome("Caneta");
            salvo.setCodigoBarras("1234567890123");
            salvo.setPreco(new BigDecimal("10.00"));

            var visualizacao = new ProdutoViewDTO(1L, "Caneta", "1234567890123", new BigDecimal("10.00"));

            when(produtomapper.toEntity(dto)).thenReturn(entidade);
            when(produtoRepository.existsByCodigoBarras(entidade.getCodigoBarras())).thenReturn(false);
            when(produtoRepository.saveAndFlush(entidade)).thenReturn(salvo);
            when(produtomapper.toViewDTO(salvo)).thenReturn(visualizacao);

            var resultado = produtoService.criarProduto(dto);

            assertNotNull(resultado);
            assertEquals(visualizacao.id(), resultado.id());
            assertEquals(visualizacao.nome(), resultado.nome());
            verify(produtoRepository).saveAndFlush(entidade);
        }

        @Test
        void criarProduto_deveLancarQuandoJaExiste() {
            var dto = new ProdutoCreateDTO(null, "Caneta", "1234567890123", new BigDecimal("10.00"));

            var entidade = new Produto();
            entidade.setNome("Caneta");
            entidade.setCodigoBarras("1234567890123");
            entidade.setPreco(new BigDecimal("10.00"));

            when(produtomapper.toEntity(dto)).thenReturn(entidade);
            when(produtoRepository.existsByCodigoBarras(entidade.getCodigoBarras())).thenReturn(true);

            assertThrows(ProdutoJaExisteException.class, () -> produtoService.criarProduto(dto));
            verify(produtoRepository, never()).saveAndFlush(any());
        }

        @Test
        void criarProduto_deveLancarQuandoPrecoInvalido() {
            var dto = new ProdutoCreateDTO(null, "Caneta", "1234567890123", BigDecimal.ZERO);

            var entidade = new Produto();
            entidade.setNome("Caneta");
            entidade.setCodigoBarras("1234567890123");
            entidade.setPreco(BigDecimal.ZERO);

            when(produtomapper.toEntity(dto)).thenReturn(entidade);

            assertThrows(PrecoDeveSerMaiorQueZeroException.class, () -> produtoService.criarProduto(dto));
            verify(produtoRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Testes de listagem de Produtos")
    class ListarProdutos {

        @Test
        void listarProdutos_deveRetornarListaMapeada () {
            var p1 = new Produto();
            p1.setId(1L);
            p1.setNome("Caneta");
            p1.setCodigoBarras("1234567890123");
            p1.setPreco(new BigDecimal("5.00"));

            var p2 = new Produto();
            p2.setId(2L);
            p2.setNome("Lapis");
            p2.setCodigoBarras("1234567890124");
            p2.setPreco(new BigDecimal("3.00"));

            var v1 = new ProdutoViewDTO(1L, "Caneta", "1234567890123", new BigDecimal("5.00"));
            var v2 = new ProdutoViewDTO(2L, "Lapis", "1234567890124", new BigDecimal("3.00"));

            when(produtoRepository.findAll()).thenReturn(List.of(p1, p2));
            when(produtomapper.toViewDTO(p1)).thenReturn(v1);
            when(produtomapper.toViewDTO(p2)).thenReturn(v2);

            var lista = produtoService.listarProdutos();

            assertEquals(2, lista.size());
            assertTrue(lista.stream().anyMatch(it -> it.id().equals(1L)));
        }

        @Test
        void buscarProdutoPorId_deveRetornarQuandoEncontrado () {
            var produto = new Produto();
            produto.setId(1L);
            produto.setNome("Caneta");
            produto.setCodigoBarras("1234567890123");
            produto.setPreco(new BigDecimal("5.00"));

            var visualizacao = new ProdutoViewDTO(1L, "Caneta", "1234567890123", new BigDecimal("5.00"));

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtomapper.toViewDTO(produto)).thenReturn(visualizacao);

            var resultado = produtoService.buscarProdutoPorId(1L);

            assertEquals(1L, resultado.id());
        }

        @Test
        void buscarProdutoPorId_deveLancarQuandoNaoEncontrado () {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.buscarProdutoPorId(99L));
        }

    }

    @Nested
    @DisplayName("Testes de atualização de Produtos")
    class  AtualizarProduto {

        @Test
        void atualizarProduto_deveAtualizarQuandoValido() {
            var dto = new ProdutoUpdateDTO("Caneta Updated", "1234567890123", new BigDecimal("12.00"));

            var produtoExistente = new Produto();
            produtoExistente.setId(1L);
            produtoExistente.setNome("Caneta");
            produtoExistente.setCodigoBarras("1234567890123");
            produtoExistente.setPreco(new BigDecimal("5.00"));

            var produtoAtualizadoEntidade = new Produto();
            produtoAtualizadoEntidade.setId(1L);
            produtoAtualizadoEntidade.setNome("Caneta Updated");
            produtoAtualizadoEntidade.setCodigoBarras("1234567890123");
            produtoAtualizadoEntidade.setPreco(new BigDecimal("12.00"));

            var visualizacao = new ProdutoViewDTO(1L, "Caneta Updated", "1234567890123", new BigDecimal("12.00"));

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            doAnswer(invocation -> {
                ProdutoUpdateDTO argDto = invocation.getArgument(0);
                Produto argEntity = invocation.getArgument(1);
                argEntity.setNome(argDto.nome());
                argEntity.setCodigoBarras(argDto.codigoBarras());
                argEntity.setPreco(argDto.preco());
                return null;
            }).when(produtomapper).updateEntityFromDTO(dto, produtoExistente);

            when(produtoRepository.saveAndFlush(produtoExistente)).thenReturn(produtoAtualizadoEntidade);
            when(produtomapper.toViewDTO(produtoAtualizadoEntidade)).thenReturn(visualizacao);

            var resultado = produtoService.atualizarProduto(1L, dto);

            assertEquals("Caneta Updated", resultado.nome());
            verify(produtoRepository).saveAndFlush(produtoExistente);
        }

        @Test
        void atualizaProdutoPorId_deveLancarQuandoNaoEncontrado() {
            var dto = new ProdutoUpdateDTO("Caneta Updated", "1234567890123", new BigDecimal("12.00"));

            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.atualizarProduto(99L, dto));
        }

        @Test
        void atualizarProduto_deveLancarQuandoPrecoDTOInvalido() {
            var dto = new ProdutoUpdateDTO("Caneta Updated", "1234567890123", BigDecimal.ZERO);
            var produtoExistente = new Produto();
            produtoExistente.setId(1L);
            produtoExistente.setNome("Caneta");
            produtoExistente.setCodigoBarras("1234567890123");
            produtoExistente.setPreco(new BigDecimal("5.00"));

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

            assertThrows(PrecoDeveSerMaiorQueZeroException.class, () -> produtoService.atualizarProduto(1L, dto));
            verify(produtoRepository, never()).saveAndFlush(any());
        }
    }
    @Nested
    @DisplayName("Testes de exclusão de Produtos")
    class ExcluirProduto {
        @Test
        void deletarProduto_deveDeletarQuandoExiste() {
            var produtoExistente = new Produto();
            produtoExistente.setId(1L);
            produtoExistente.setNome("Caneta");
            produtoExistente.setCodigoBarras("1234567890123");
            produtoExistente.setPreco(new BigDecimal("5.00"));

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

            produtoService.deletarProduto(1L);

            verify(produtoRepository).deleteById(1L);
        }

        @Test
        void deletarProduto_deveLancarQuandoNaoExiste() {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.deletarProduto(99L));
            verify(produtoRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Testes de Estoque do Produto")
    class EstoqueProduto {
        @Test
        void buscarEstoqueDoProduto_deveRetornarQuandoEncontrado() {
            Long produtoId = 1L;

            when(produtoRepository.existsById(produtoId)).thenReturn(true);

            var produto = new Produto();
            produto.setId(produtoId);

            var estoque = new br.com.kau4dev.appProdutos.model.Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setObservacao(false);

            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            var view = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO(10L, 100, false, produtoId);
            when(estoqueMapper.toViewDTO(estoque)).thenReturn(view);

            var resultado = produtoService.buscarEstoqueDoProduto(produtoId);

            assertEquals(10L, resultado.id());
            assertEquals(100, resultado.quantidade());
            assertEquals(produtoId, resultado.produtoId());
        }

        @Test
        void buscarEstoqueDoProduto_deveLancarQuandoProdutoNaoExiste() {
            Long produtoId = 99L;
            when(produtoRepository.existsById(produtoId)).thenReturn(false);
            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.buscarEstoqueDoProduto(produtoId));
        }

        @Test
        void buscarEstoqueDoProduto_deveLancarQuandoNaoEncontrado() {
            Long produtoId = 1L;
            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> produtoService.buscarEstoqueDoProduto(produtoId));
        }

        @Test
        void criarEstoqueDoProduto_deveSalvarQuandoValido() {
            Long produtoId = 1L;
            var dto = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO(100, false);

            var produto = new Produto();
            produto.setId(produtoId);
            produto.setNome("Caneta");
            produto.setCodigoBarras("1234567890123");
            produto.setPreco(new BigDecimal("10.00"));

            when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
            when(estoqueRepository.existsByProdutoId(produtoId)).thenReturn(false);

            var entidade = new br.com.kau4dev.appProdutos.model.Estoque();
            entidade.setQuantidade(100);
            entidade.setObservacao(false);
            when(estoqueMapper.toEntity(dto)).thenReturn(entidade);

            var salvo = new br.com.kau4dev.appProdutos.model.Estoque();
            salvo.setId(10L);
            salvo.setProduto(produto);
            salvo.setQuantidade(100);
            salvo.setObservacao(false);
            when(estoqueRepository.saveAndFlush(any())).thenReturn(salvo);

            var view = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO(10L, 100, false, produtoId);
            when(estoqueMapper.toViewDTO(salvo)).thenReturn(view);

            var resultado = produtoService.criarEstoqueDoProduto(produtoId, dto);

            assertEquals(10L, resultado.id());
            assertEquals(100, resultado.quantidade());
            assertEquals(produtoId, resultado.produtoId());
            verify(estoqueRepository).saveAndFlush(any());
        }

        @Test
        void criarEstoqueDoProduto_deveLancarQuandoProdutoNaoExiste() {
            Long produtoId = 99L;
            var dto = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO(100, false);
            when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());
            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.criarEstoqueDoProduto(produtoId, dto));
        }

        @Test
        void criarEstoqueDoProduto_deveLancarQuandoJaExiste() {
            Long produtoId = 1L;
            var dto = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO(100, false);

            var produto = new Produto();
            produto.setId(produtoId);
            when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
            when(estoqueRepository.existsByProdutoId(produtoId)).thenReturn(true);

            assertThrows(RuntimeException.class, () -> produtoService.criarEstoqueDoProduto(produtoId, dto));
            verify(estoqueRepository, never()).saveAndFlush(any());
        }

        @Test
        void atualizarEstoqueDoProduto_deveAtualizarQuandoValido() {
            Long produtoId = 1L;
            var dto = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO(150, true);

            when(produtoRepository.existsById(produtoId)).thenReturn(true);

            var produto = new Produto();
            produto.setId(produtoId);

            var estoque = new br.com.kau4dev.appProdutos.model.Estoque();
            estoque.setId(10L);
            estoque.setProduto(produto);
            estoque.setQuantidade(100);
            estoque.setObservacao(false);

            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque));

            doAnswer(invocation -> {
                var argDto = (br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO) invocation.getArgument(0);
                var argEstoque = (br.com.kau4dev.appProdutos.model.Estoque) invocation.getArgument(1);
                argEstoque.setQuantidade(argDto.quantidade());
                argEstoque.setObservacao(argDto.observacao());
                return null;
            }).when(estoqueMapper).updateEntityFromDTO(eq(dto), any(br.com.kau4dev.appProdutos.model.Estoque.class));

            var atualizado = new br.com.kau4dev.appProdutos.model.Estoque();
            atualizado.setId(10L);
            atualizado.setProduto(produto);
            atualizado.setQuantidade(150);
            atualizado.setObservacao(true);
            when(estoqueRepository.saveAndFlush(estoque)).thenReturn(atualizado);

            var view = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO(10L, 150, true, produtoId);
            when(estoqueMapper.toViewDTO(atualizado)).thenReturn(view);

            var resultado = produtoService.atualizarEstoqueDoProduto(produtoId, dto);

            assertEquals(150, resultado.quantidade());
            assertTrue(resultado.observacao());
            verify(estoqueRepository).saveAndFlush(estoque);
        }

        @Test
        void atualizarEstoqueDoProduto_deveLancarQuandoProdutoNaoExiste() {
            Long produtoId = 99L;
            var dto = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO(150, true);
            when(produtoRepository.existsById(produtoId)).thenReturn(false);
            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.atualizarEstoqueDoProduto(produtoId, dto));
            verify(estoqueRepository, never()).saveAndFlush(any());
        }

        @Test
        void atualizarEstoqueDoProduto_deveLancarQuandoNaoEncontrado() {
            Long produtoId = 1L;
            var dto = new br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO(150, true);
            when(produtoRepository.existsById(produtoId)).thenReturn(true);
            when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> produtoService.atualizarEstoqueDoProduto(produtoId, dto));
        }
    }
}
