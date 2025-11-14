package br.com.kau4dev.appProdutos.mapper;

import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueCreateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueUpdateDTO;
import br.com.kau4dev.appProdutos.dto.estoqueDTO.EstoqueViewDTO;
import br.com.kau4dev.appProdutos.model.Estoque;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    EstoqueViewDTO toViewDTO(Estoque estoque);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    Estoque toEntity(EstoqueCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    void updateEntityFromDTO(EstoqueUpdateDTO dto, @MappingTarget Estoque estoque);
}
