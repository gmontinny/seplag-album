package br.com.seplagalbum.mapper;

import br.com.seplagalbum.dto.ArtistaRequest;
import br.com.seplagalbum.dto.ArtistaResponse;
import br.com.seplagalbum.model.Artista;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArtistaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "albuns", ignore = true)
    Artista toEntity(ArtistaRequest request);

    ArtistaResponse toResponse(Artista artista);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "albuns", ignore = true)
    void updateEntity(ArtistaRequest request, @MappingTarget Artista artista);
}
