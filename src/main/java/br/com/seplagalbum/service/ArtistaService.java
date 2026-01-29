package br.com.seplagalbum.service;

import br.com.seplagalbum.model.Artista;
import br.com.seplagalbum.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    private final ArtistaRepository repository;

    public List<Artista> listar(String nome, String ordem) {
        Sort sort = Sort.by("nome");
        if ("desc".equalsIgnoreCase(ordem)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        if (nome != null && !nome.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(nome, sort);
        }
        return repository.findAll(sort);
    }

    public Artista buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Artista não encontrado"));
    }

    public Artista salvar(Artista artista) {
        return repository.save(artista);
    }
}
