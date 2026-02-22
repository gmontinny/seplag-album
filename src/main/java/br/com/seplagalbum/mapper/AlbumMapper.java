package br.com.seplagalbum.mapper;

import br.com.seplagalbum.dto.AlbumRequest;
import br.com.seplagalbum.dto.AlbumResponse;
import br.com.seplagalbum.model.Album;
import br.com.seplagalbum.model.Artista;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.seplagalbum.repository.ArtistaRepository;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ArtistaMapper.class})
public abstract class AlbumMapper {

    @Autowired
    protected ArtistaRepository artistaRepository;

    @Mapping(target = "artistas", source = "artistaIds", qualifiedByName = "idsToArtistas")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capaUrl", ignore = true)
    public abstract Album toEntity(AlbumRequest request);

    public abstract AlbumResponse toResponse(Album album);

    @Mapping(target = "artistas", source = "artistaIds", qualifiedByName = "idsToArtistas")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capaUrl", ignore = true)
    public abstract void updateEntity(AlbumRequest request, @MappingTarget Album album);

    @Named("idsToArtistas")
    protected Set<Artista> idsToArtistas(Set<Long> artistaIds) {
        if (artistaIds == null || artistaIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(artistaRepository.findAllById(artistaIds));
    }
}
