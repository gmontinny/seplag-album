package br.com.seplagalbum.mapper;

import br.com.seplagalbum.dto.ArtistaRequest;
import br.com.seplagalbum.dto.ArtistaResponse;
import br.com.seplagalbum.model.Artista;
import org.springframework.stereotype.Component;

@Component
public class ArtistaMapper {

    public Artista toEntity(ArtistaRequest request) {
        Artista artista = new Artista();
        artista.setNome(request.getNome());
        artista.setTipo(request.getTipo());
        return artista;
    }

    public ArtistaResponse toResponse(Artista artista) {
        return new ArtistaResponse(
                artista.getId(),
                artista.getNome(),
                artista.getTipo()
        );
    }

    public void updateEntity(ArtistaRequest request, Artista artista) {
        artista.setNome(request.getNome());
        artista.setTipo(request.getTipo());
    }
}
