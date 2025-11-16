package br.com.kau4dev.appProdutos.controller;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.service.EstoqueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Estoque", description = "Operações relacionadas a Estoque")
@RestController
@RequestMapping("api/produtos")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("{id}/estoque")
    public ResponseEntity<EstoqueViewDTO> buscarEstoqueDoProduto(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarEstoqueDoProduto(id));
    }

    @PutMapping("{id}/estoque")
    public ResponseEntity<EstoqueViewDTO> atualizarEstoqueDoProduto(
            @PathVariable Long id,
            @RequestBody @Valid EstoqueUpdateDTO estoqueUpdateDTO) {
        EstoqueViewDTO estoqueAtualizado = estoqueService.atualizarEstoqueDoProduto(id, estoqueUpdateDTO);
        return ResponseEntity.ok(estoqueAtualizado);
    }

    @PostMapping("{id}/estoque/adicionar")
    public ResponseEntity<Map<String, String>> adicionarEstoque(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        int quantidade = (int) request.get("quantidade");
        String motivo = (String) request.getOrDefault("motivo", "Entrada de estoque");

        estoqueService.adicionarEstoque(id, quantidade, motivo);
        return ResponseEntity.ok(Map.of("mensagem", "Estoque adicionado com sucesso"));
    }

    @PostMapping("{id}/estoque/remover")
    public ResponseEntity<Map<String, String>> removerEstoque(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        int quantidade = (int) request.get("quantidade");
        String motivo = (String) request.getOrDefault("motivo", "Saída de estoque");

        estoqueService.removerEstoque(id, quantidade, motivo);
        return ResponseEntity.ok(Map.of("mensagem", "Estoque removido com sucesso"));
    }

    @GetMapping("{id}/estoque/verificar-disponibilidade")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam int quantidade) {
        boolean disponivel = estoqueService.temEstoqueDisponivel(id, quantidade);
        return ResponseEntity.ok(Map.of(
            "disponivel", disponivel,
            "quantidade", quantidade,
            "produtoId", id
        ));
    }
}
