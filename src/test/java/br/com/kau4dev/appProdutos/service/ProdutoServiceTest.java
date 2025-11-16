package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoCreateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoUpdateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoViewDTO;
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
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoMapper produtomapper;

    @InjectMocks
    private ProdutoService produtoService;

    @Nested
    @DisplayName("Testes de Criação de Produto")
    class CriarProduto {
        @Test
        void criarProduto_deveSalvarQuandoValido() throws ProdutoJaExisteException {
            long categoriaId = 1L;
            var dto = new ProdutoCreateDTO(
                    null,
                    "Caneta",
                    "SKU-CAN-001",
                    "1234567890123",
                    "Caneta azul",
                    new BigDecimal("10.00"),
                    new BigDecimal("7.50"),
                    categoriaId
            );

            var categoria = new Categoria();
            categoria.setId(categoriaId);

            var entidade = new Produto();
            entidade.setNome("Caneta");
            entidade.setSku("SKU-CAN-001");
            entidade.setCodigoBarras("1234567890123");
            entidade.setDescricao("Caneta azul");
            entidade.setPreco(new BigDecimal("10.00"));
            entidade.setPrecoCusto(new BigDecimal("7.50"));

            var salvo = new Produto();
            salvo.setId(1L);
            salvo.setNome("Caneta");
            salvo.setSku("SKU-CAN-001");
            salvo.setCodigoBarras("1234567890123");
            salvo.setDescricao("Caneta azul");
            salvo.setPreco(new BigDecimal("10.00"));
            salvo.setPrecoCusto(new BigDecimal("7.50"));
            salvo.setIdCategoria(categoria);

            var visualizacao = new ProdutoViewDTO(
                    1L,
                    "Caneta",
                    "SKU-CAN-001",
                    "1234567890123",
                    "Caneta azul",
                    new BigDecimal("10.00"),
                    new BigDecimal("7.50"),
                    categoriaId
            );

            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
            when(produtomapper.toEntity(dto)).thenReturn(entidade);
            when(produtoRepository.existsByCodigoBarras(entidade.getCodigoBarras())).thenReturn(false);
            when(produtoRepository.saveAndFlush(entidade)).thenReturn(salvo);
            when(produtomapper.toViewDTO(salvo)).thenReturn(visualizacao);

            var resultado = produtoService.criarProduto(dto);

            assertNotNull(resultado);
            assertEquals(visualizacao.id(), resultado.id());
            assertEquals(visualizacao.nome(), resultado.nome());
            verify(produtoRepository).saveAndFlush(entidade);
            verify(estoqueRepository).save(any(Estoque.class));
        }

        @Test
        void criarProduto_deveLancarQuandoJaExiste() {
            long categoriaId = 1L;
            var dto = new ProdutoCreateDTO(
                    null,
                    "Caneta",
                    "SKU-CAN-001",
                    "1234567890123",
                    "Caneta azul",
                    new BigDecimal("10.00"),
                    new BigDecimal("7.50"),
                    categoriaId
            );

            var categoria = new Categoria();
            categoria.setId(categoriaId);

            var entidade = new Produto();
            entidade.setNome("Caneta");
            entidade.setSku("SKU-CAN-001");
            entidade.setCodigoBarras("1234567890123");
            entidade.setDescricao("Caneta azul");
            entidade.setPreco(new BigDecimal("10.00"));
            entidade.setPrecoCusto(new BigDecimal("7.50"));

            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
            when(produtomapper.toEntity(dto)).thenReturn(entidade);
            when(produtoRepository.existsByCodigoBarras(entidade.getCodigoBarras())).thenReturn(true);

            assertThrows(ProdutoJaExisteException.class, () -> produtoService.criarProduto(dto));
            verify(produtoRepository, never()).saveAndFlush(any());
            verify(estoqueRepository, never()).save(any());
        }

        @Test
        void criarProduto_deveLancarQuandoPrecoInvalido() {
            long categoriaId = 1L;
            var dto = new ProdutoCreateDTO(
                    null,
                    "Caneta",
                    "SKU-CAN-001",
                    "1234567890123",
                    "Caneta azul",
                    BigDecimal.ZERO,
                    new BigDecimal("7.50"),
                    categoriaId
            );

            assertThrows(PrecoDeveSerMaiorQueZeroException.class, () -> produtoService.criarProduto(dto));
            verify(produtoRepository, never()).saveAndFlush(any());
            verify(estoqueRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de listagem de Produtos")
    class ListarProdutos {

        @Test
        void listarProdutos_deveRetornarListaMapeada() {
            var c = new Categoria();
            c.setId(1L);

            var p1 = new Produto();
            p1.setId(1L);
            p1.setNome("Caneta");
            p1.setSku("SKU-CAN-001");
            p1.setCodigoBarras("1234567890123");
            p1.setDescricao("Caneta azul");
            p1.setPreco(new BigDecimal("5.00"));
            p1.setPrecoCusto(new BigDecimal("3.00"));
            p1.setIdCategoria(c);

            var p2 = new Produto();
            p2.setId(2L);
            p2.setNome("Lapis");
            p2.setSku("SKU-LAP-001");
            p2.setCodigoBarras("1234567890124");
            p2.setDescricao("Lapis HB");
            p2.setPreco(new BigDecimal("3.00"));
            p2.setPrecoCusto(new BigDecimal("1.50"));
            p2.setIdCategoria(c);

            var v1 = new ProdutoViewDTO(1L, "Caneta", "SKU-CAN-001", "1234567890123", "Caneta azul", new BigDecimal("5.00"), new BigDecimal("3.00"), 1L);
            var v2 = new ProdutoViewDTO(2L, "Lapis", "SKU-LAP-001", "1234567890124", "Lapis HB", new BigDecimal("3.00"), new BigDecimal("1.50"), 1L);

            when(produtoRepository.findAll()).thenReturn(List.of(p1, p2));
            when(produtomapper.toViewDTO(p1)).thenReturn(v1);
            when(produtomapper.toViewDTO(p2)).thenReturn(v2);

            var lista = produtoService.listarProdutos();

            assertEquals(2, lista.size());
            assertTrue(lista.stream().anyMatch(it -> it.id().equals(1L)));
        }

        @Test
        void buscarProdutoPorId_deveRetornarQuandoEncontrado() {
            var c = new Categoria();
            c.setId(1L);

            var produto = new Produto();
            produto.setId(1L);
            produto.setNome("Caneta");
            produto.setSku("SKU-CAN-001");
            produto.setCodigoBarras("1234567890123");
            produto.setDescricao("Caneta azul");
            produto.setPreco(new BigDecimal("5.00"));
            produto.setPrecoCusto(new BigDecimal("3.00"));
            produto.setIdCategoria(c);

            var visualizacao = new ProdutoViewDTO(1L, "Caneta", "SKU-CAN-001", "1234567890123", "Caneta azul", new BigDecimal("5.00"), new BigDecimal("3.00"), 1L);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtomapper.toViewDTO(produto)).thenReturn(visualizacao);

            var resultado = produtoService.buscarProdutoPorId(1L);

            assertEquals(1L, resultado.id());
            assertEquals("SKU-CAN-001", resultado.sku());
        }

        @Test
        void buscarProdutoPorId_deveLancarQuandoNaoEncontrado() {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.buscarProdutoPorId(99L));
        }

    }

    @Nested
    @DisplayName("Testes de atualização de Produtos")
    class AtualizarProduto {

        @Test
        void atualizarProduto_deveAtualizarQuandoValido() {
            long categoriaId = 1L;
            var dto = new ProdutoUpdateDTO(
                    "Caneta Updated",
                    "SKU-CAN-002",
                    "1234567890123",
                    "Caneta azul ponta fina",
                    new BigDecimal("12.00"),
                    new BigDecimal("8.00"),
                    categoriaId
            );

            var c = new Categoria();
            c.setId(categoriaId);

            var produtoExistente = new Produto();
            produtoExistente.setId(1L);
            produtoExistente.setNome("Caneta");
            produtoExistente.setSku("SKU-CAN-001");
            produtoExistente.setCodigoBarras("1234567890123");
            produtoExistente.setDescricao("Caneta azul");
            produtoExistente.setPreco(new BigDecimal("5.00"));
            produtoExistente.setPrecoCusto(new BigDecimal("3.00"));
            produtoExistente.setIdCategoria(c);

            var produtoAtualizadoEntidade = new Produto();
            produtoAtualizadoEntidade.setId(1L);
            produtoAtualizadoEntidade.setNome("Caneta Updated");
            produtoAtualizadoEntidade.setSku("SKU-CAN-002");
            produtoAtualizadoEntidade.setCodigoBarras("1234567890123");
            produtoAtualizadoEntidade.setDescricao("Caneta azul ponta fina");
            produtoAtualizadoEntidade.setPreco(new BigDecimal("12.00"));
            produtoAtualizadoEntidade.setPrecoCusto(new BigDecimal("8.00"));
            produtoAtualizadoEntidade.setIdCategoria(c);

            var visualizacao = new ProdutoViewDTO(1L, "Caneta Updated", "SKU-CAN-002", "1234567890123", "Caneta azul ponta fina", new BigDecimal("12.00"), new BigDecimal("8.00"), categoriaId);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            doAnswer(invocation -> {
                ProdutoUpdateDTO argDto = invocation.getArgument(0);
                Produto argEntity = invocation.getArgument(1);
                argEntity.setNome(argDto.nome());
                argEntity.setSku(argDto.sku());
                argEntity.setCodigoBarras(argDto.codigoBarras());
                argEntity.setDescricao(argDto.descricao());
                argEntity.setPreco(argDto.preco());
                argEntity.setPrecoCusto(argDto.precoCusto());
                return null;
            }).when(produtomapper).updateEntityFromDTO(eq(dto), eq(produtoExistente));

            when(produtoRepository.saveAndFlush(produtoExistente)).thenReturn(produtoAtualizadoEntidade);
            when(produtomapper.toViewDTO(produtoAtualizadoEntidade)).thenReturn(visualizacao);

            var resultado = produtoService.atualizarProduto(1L, dto);

            assertEquals("Caneta Updated", resultado.nome());
            assertEquals("SKU-CAN-002", resultado.sku());
            verify(produtoRepository).saveAndFlush(produtoExistente);
        }

        @Test
        void atualizaProdutoPorId_deveLancarQuandoNaoEncontrado() {
            var dto = new ProdutoUpdateDTO(
                    "Caneta Updated",
                    "SKU-CAN-002",
                    "1234567890123",
                    "Caneta azul ponta fina",
                    new BigDecimal("12.00"),
                    new BigDecimal("8.00"),
                    1L
            );

            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IdProdutoNaoEncontradoException.class, () -> produtoService.atualizarProduto(99L, dto));
        }

        @Test
        void atualizarProduto_deveLancarQuandoPrecoDTOInvalido() {
            var dto = new ProdutoUpdateDTO(
                    "Caneta Updated",
                    "SKU-CAN-002",
                    "1234567890123",
                    "Caneta azul ponta fina",
                    BigDecimal.ZERO,
                    new BigDecimal("8.00"),
                    1L
            );
            var produtoExistente = new Produto();
            produtoExistente.setId(1L);

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
}
