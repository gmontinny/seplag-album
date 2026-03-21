package br.com.seplagalbum.controller;

import br.com.seplagalbum.dto.RegionalResponse;
import br.com.seplagalbum.model.Regional;
import br.com.seplagalbum.repository.RegionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionalControllerTest {

    @Mock
    private RegionalRepository repository;

    @InjectMocks
    private RegionalController controller;

    @Test
    void deveListarTodasAsRegionais() {
        Regional r1 = new Regional(1L, 101, "Regional 1", true);
        Regional r2 = new Regional(2L, 102, "Regional 2", false);

        when(repository.findAll()).thenReturn(List.of(r1, r2));

        ResponseEntity<CollectionModel<RegionalResponse>> response = controller.listar(false);

        assertEquals(2, response.getBody().getContent().size());
        assertEquals("Regional 1", response.getBody().getContent().iterator().next().getNome());
    }

    @Test
    void deveListarApenasRegionaisAtivas() {
        Regional r1 = new Regional(1L, 101, "Regional 1", true);

        when(repository.findByAtivoTrue()).thenReturn(List.of(r1));

        ResponseEntity<CollectionModel<RegionalResponse>> response = controller.listar(true);

        assertEquals(1, response.getBody().getContent().size());
        assertEquals(true, response.getBody().getContent().iterator().next().getAtivo());
    }

    @Test
    void deveBuscarRegionalPorId() {
        Regional r1 = new Regional(1L, 101, "Regional 1", true);

        when(repository.findById(1L)).thenReturn(Optional.of(r1));

        ResponseEntity<RegionalResponse> response = controller.buscarPorId(1L);

        assertNotNull(response.getBody());
        assertEquals("Regional 1", response.getBody().getNome());
        assertEquals(101, response.getBody().getId());
    }
}
