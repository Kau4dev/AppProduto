package br.com.kau4dev.appProdutos.controller;


import java.util.List;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoCreateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoUpdateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoViewDTO;
import br.com.kau4dev.appProdutos.exception.ProdutoJaExisteException;
import br.com.kau4dev.appProdutos.service.ProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Produtos", description = "Operações relacionadas a Produtos")
@RestController
@RequestMapping("api/Produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoViewDTO> criarUsuario(@RequestBody @Valid ProdutoCreateDTO produtoCreateDTO) throws ProdutoJaExisteException {
        ProdutoViewDTO produtoCriado = produtoService.criarProduto(produtoCreateDTO);
        return ResponseEntity.status(201).body(produtoCriado);
    }


    @GetMapping("{id}")
    public ResponseEntity<ProdutoViewDTO> buscarProdutoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProdutoViewDTO>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarProdutos());
    }


    @PutMapping("{id}")
    public ResponseEntity<ProdutoViewDTO> atualizarProduto(@PathVariable Long id, @RequestBody @Valid ProdutoUpdateDTO produtoUpdateDTO) {
        ProdutoViewDTO atualizado = produtoService.atualizarProduto(id, produtoUpdateDTO);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.status(204).build();
    }

}
