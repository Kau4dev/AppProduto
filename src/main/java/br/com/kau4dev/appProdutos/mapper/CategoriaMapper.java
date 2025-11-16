package br.com.kau4dev.appProdutos.mapper;

import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaCreateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaUpdateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaViewDTO;
import br.com.kau4dev.appProdutos.model.Categoria;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    Categoria toEntity(CategoriaCreateDTO categoriaCreateDTO);

    CategoriaViewDTO toViewDTO(Categoria categoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    void updateEntityFromDTO(CategoriaUpdateDTO dto, @MappingTarget Categoria entity);
}
