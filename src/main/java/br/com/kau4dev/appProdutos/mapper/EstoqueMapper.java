package br.com.kau4dev.appProdutos.mapper;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.model.Estoque;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "nomeProduto", source = "produto.nome")
    EstoqueViewDTO toViewDTO(Estoque estoque);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "transacoes", ignore = true)
    Estoque toEntity(EstoqueCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "transacoes", ignore = true)
    void updateEntityFromDTO(EstoqueUpdateDTO dto, @MappingTarget Estoque estoque);
}
