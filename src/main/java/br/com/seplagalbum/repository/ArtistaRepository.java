package br.com.seplagalbum.repository;

import br.com.seplagalbum.model.Artista;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    List<Artista> findByNomeContainingIgnoreCase(String nome, Sort sort);
}
