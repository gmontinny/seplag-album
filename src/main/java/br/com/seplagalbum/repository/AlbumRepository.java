package br.com.seplagalbum.repository;

import br.com.seplagalbum.model.Album;
import br.com.seplagalbum.model.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    
    @EntityGraph(attributePaths = "artistas")
    @Query("SELECT a FROM Album a JOIN a.artistas art WHERE art.tipo = :tipo")
    Page<Album> findByArtistasTipo(Artista.TipoArtista tipo, Pageable pageable);
    
    @EntityGraph(attributePaths = "artistas")
    Page<Album> findAll(Pageable pageable);
}
