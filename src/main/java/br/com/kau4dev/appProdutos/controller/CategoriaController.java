package br.com.kau4dev.appProdutos.controller;

import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaCreateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaUpdateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaViewDTO;
import br.com.kau4dev.appProdutos.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaViewDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listarCategoria());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaViewDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarCategoriaPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaViewDTO> criar(@Valid @RequestBody CategoriaCreateDTO dto) {
        CategoriaViewDTO criada = categoriaService.criarCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaViewDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaUpdateDTO dto) {
        return ResponseEntity.ok(categoriaService.atualizarCategoria(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}

