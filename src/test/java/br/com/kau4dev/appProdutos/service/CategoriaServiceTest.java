package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaCreateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaUpdateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaViewDTO;
import br.com.kau4dev.appProdutos.mapper.CategoriaMapper;
import br.com.kau4dev.appProdutos.model.Categoria;
import br.com.kau4dev.appProdutos.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @Nested
    @DisplayName("Criação de categoria")
    class CriarCategoria {
        @Test
        void criarCategoria_deveSalvarQuandoValida() {
            var dto = new CategoriaCreateDTO("Papelaria");
            var entidade = new Categoria();
            entidade.setNome("Papelaria");
            var salvo = new Categoria();
            salvo.setId(1L);
            salvo.setNome("Papelaria");
            var view = new CategoriaViewDTO(1L, "Papelaria");

            when(categoriaRepository.existsByNome("Papelaria")).thenReturn(false);
            when(categoriaMapper.toEntity(dto)).thenReturn(entidade);
            when(categoriaRepository.save(entidade)).thenReturn(salvo);
            when(categoriaMapper.toViewDTO(salvo)).thenReturn(view);

            var resultado = categoriaService.criarCategoria(dto);

            assertEquals(1L, resultado.id());
            assertEquals("Papelaria", resultado.nome());
            verify(categoriaRepository).save(entidade);
        }

        @Test
        void criarCategoria_deveLancarQuandoNomeDuplicado() {
            var dto = new CategoriaCreateDTO("Papelaria");

            when(categoriaRepository.existsByNome("Papelaria")).thenReturn(true);

            assertThrows(RuntimeException.class, () -> categoriaService.criarCategoria(dto));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Listagem e busca de categoria")
    class ListarBuscarCategoria {
        @Test
        void listarCategoria_deveRetornarListaMapeada() {
            var c1 = new Categoria();
            c1.setId(1L);
            c1.setNome("Papelaria");
            var c2 = new Categoria();
            c2.setId(2L);
            c2.setNome("Livros");

            when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
            when(categoriaMapper.toViewDTO(c1)).thenReturn(new CategoriaViewDTO(1L, "Papelaria"));
            when(categoriaMapper.toViewDTO(c2)).thenReturn(new CategoriaViewDTO(2L, "Livros"));

            var lista = categoriaService.listarCategoria();

            assertEquals(2, lista.size());
            assertTrue(lista.stream().anyMatch(it -> it.nome().equals("Papelaria")));
        }

        @Test
        void buscarCategoriaPorId_deveRetornarQuandoEncontrada() {
            var entidade = new Categoria();
            entidade.setId(1L);
            entidade.setNome("Papelaria");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(entidade));
            when(categoriaMapper.toViewDTO(entidade)).thenReturn(new CategoriaViewDTO(1L, "Papelaria"));

            var view = categoriaService.buscarCategoriaPorId(1L);

            assertEquals(1L, view.id());
            assertEquals("Papelaria", view.nome());
        }

        @Test
        void buscarCategoriaPorId_deveLancarQuandoNaoEncontrada() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> categoriaService.buscarCategoriaPorId(99L));
        }
    }

    @Nested
    @DisplayName("Atualização de categoria")
    class AtualizarCategoria {
        @Test
        void atualizarCategoria_deveAtualizarQuandoValida() {
            var entidade = new Categoria();
            entidade.setId(1L);
            entidade.setNome("Papelaria");
            var dto = new CategoriaUpdateDTO("Papelaria Premium");
            var salvo = new Categoria();
            salvo.setId(1L);
            salvo.setNome("Papelaria Premium");
            var view = new CategoriaViewDTO(1L, "Papelaria Premium");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(entidade));
            doAnswer(inv -> {
                entidade.setNome(dto.nome());
                return null;
            }).when(categoriaMapper).updateEntityFromDTO(eq(dto), eq(entidade));
            when(categoriaRepository.save(entidade)).thenReturn(salvo);
            when(categoriaMapper.toViewDTO(salvo)).thenReturn(view);

            var resultado = categoriaService.atualizarCategoria(1L, dto);

            assertEquals("Papelaria Premium", resultado.nome());
            verify(categoriaRepository).save(entidade);
        }

        @Test
        void atualizarCategoria_deveLancarQuandoNaoEncontrada() {
            var dto = new CategoriaUpdateDTO("Papelaria Premium");

            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> categoriaService.atualizarCategoria(99L, dto));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Exclusão de categoria")
    class DeletarCategoria {
        @Test
        void deletarCategoria_deveDeletarQuandoExiste() {
            when(categoriaRepository.existsById(1L)).thenReturn(true);

            categoriaService.deletarCategoria(1L);

            verify(categoriaRepository).deleteById(1L);
        }

        @Test
        void deletarCategoria_deveLancarQuandoNaoExiste() {
            when(categoriaRepository.existsById(99L)).thenReturn(false);

            assertThrows(RuntimeException.class, () -> categoriaService.deletarCategoria(99L));
            verify(categoriaRepository, never()).deleteById(any());
        }
    }
}

