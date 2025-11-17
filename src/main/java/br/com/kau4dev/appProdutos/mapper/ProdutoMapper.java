package br.com.kau4dev.appProdutos.mapper;

import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoCreateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoUpdateDTO;
import br.com.kau4dev.appProdutos.dto.produtoDTO.ProdutoViewDTO;
import br.com.kau4dev.appProdutos.model.Produto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "estoque", ignore = true)
    Produto toEntity(ProdutoCreateDTO produtoCreateDTO);

    @Mapping(target = "categoriaId", source = "categoria.id")
    ProdutoViewDTO toViewDTO(Produto produto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "estoque", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "codigoBarras", ignore = true)
    void updateEntityFromDTO(ProdutoUpdateDTO dto, @MappingTarget Produto entity);
}
