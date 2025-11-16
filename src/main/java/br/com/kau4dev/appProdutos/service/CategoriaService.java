package br.com.kau4dev.appProdutos.service;

import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaCreateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaUpdateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaViewDTO;
import br.com.kau4dev.appProdutos.mapper.CategoriaMapper;
import br.com.kau4dev.appProdutos.model.Categoria;
import br.com.kau4dev.appProdutos.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional
    public CategoriaViewDTO criarCategoria(CategoriaCreateDTO categoriaCreateDTO) {
        if (categoriaRepository.existsByNome(categoriaCreateDTO.nome())) {
            throw new RuntimeException("Já existe uma categoria com este nome");
        }
        Categoria categoria = categoriaMapper.toEntity(categoriaCreateDTO);
        Categoria salva = categoriaRepository.save(categoria);
        return categoriaMapper.toViewDTO(salva);
    }

    public List<CategoriaViewDTO> listarCategoria() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toViewDTO)
                .toList();
    }

    public CategoriaViewDTO buscarCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        return categoriaMapper.toViewDTO(categoria);
    }

    @Transactional
    public CategoriaViewDTO atualizarCategoria(Long id, CategoriaUpdateDTO categoriaUpdateDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        categoriaMapper.updateEntityFromDTO(categoriaUpdateDTO, categoria);
        Categoria atualizada = categoriaRepository.save(categoria);
        return categoriaMapper.toViewDTO(atualizada);
    }

    @Transactional
    public void deletarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}
