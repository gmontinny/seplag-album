package br.com.seplagalbum.service;

import br.com.seplagalbum.model.Artista;
import br.com.seplagalbum.repository.ArtistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository repository;

    @InjectMocks
    private ArtistaService service;

    @Test
    void deveListarArtistasOrdenados() {
        Artista a1 = new Artista(1L, "Serj Tankian", Artista.TipoArtista.CANTOR, null);
        Artista a2 = new Artista(2L, "Mike Shinoda", Artista.TipoArtista.CANTOR, null);
        
        when(repository.findAll(Sort.by("nome").ascending())).thenReturn(List.of(a2, a1));

        List<Artista> resultado = service.listar(null, "asc");

        assertEquals(2, resultado.size());
        assertEquals("Mike Shinoda", resultado.get(0).getNome());
    }

    @Test
    void deveBuscarPorId() {
        Artista a1 = new Artista(1L, "Serj Tankian", Artista.TipoArtista.CANTOR, null);
        when(repository.findById(1L)).thenReturn(Optional.of(a1));

        Artista resultado = service.buscarPorId(1L);

        assertEquals("Serj Tankian", resultado.getNome());
    }
}
