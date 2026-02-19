package br.com.seplagalbum.mapper;

import br.com.seplagalbum.dto.AlbumRequest;
import br.com.seplagalbum.dto.AlbumResponse;
import br.com.seplagalbum.model.Album;
import br.com.seplagalbum.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlbumMapper {

    private final ArtistaMapper artistaMapper;
    private final ArtistaRepository artistaRepository;

    public Album toEntity(AlbumRequest request) {
        Album album = new Album();
        album.setTitulo(request.getTitulo());
        
        if (request.getArtistaIds() != null && !request.getArtistaIds().isEmpty()) {
            album.setArtistas(new HashSet<>(artistaRepository.findAllById(request.getArtistaIds())));
        }
        
        return album;
    }

    public AlbumResponse toResponse(Album album) {
        AlbumResponse response = new AlbumResponse();
        response.setId(album.getId());
        response.setTitulo(album.getTitulo());
        response.setCapaUrl(album.getCapaUrl());
        
        if (album.getArtistas() != null) {
            response.setArtistas(
                album.getArtistas().stream()
                    .map(artistaMapper::toResponse)
                    .collect(Collectors.toSet())
            );
        }
        
        return response;
    }

    public void updateEntity(AlbumRequest request, Album album) {
        album.setTitulo(request.getTitulo());
        
        if (request.getArtistaIds() != null) {
            album.setArtistas(new HashSet<>(artistaRepository.findAllById(request.getArtistaIds())));
        }
    }
}
