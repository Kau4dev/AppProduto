package br.com.kau4dev.appProdutos.mapper;

import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaCreateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaUpdateDTO;
import br.com.kau4dev.appProdutos.dto.CategoriaDTO.CategoriaViewDTO;
import br.com.kau4dev.appProdutos.model.Categoria;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaCreateDTO categoriaCreateDTO);

    CategoriaViewDTO toViewDTO(Categoria categoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CategoriaUpdateDTO dto, @MappingTarget Categoria entity);
}
