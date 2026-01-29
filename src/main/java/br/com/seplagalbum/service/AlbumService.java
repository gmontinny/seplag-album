package br.com.seplagalbum.service;

import br.com.seplagalbum.model.Album;
import br.com.seplagalbum.model.Artista;
import br.com.seplagalbum.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public Page<Album> listar(Artista.TipoArtista tipo, Pageable pageable) {
        if (tipo != null) {
            return repository.findByArtistasTipo(tipo, pageable);
        }
        return repository.findAll(pageable);
    }

    public Album buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Álbum não encontrado"));
    }

    public Album salvar(Album album) {
        boolean isNew = album.getId() == null;
        Album salvo = repository.save(album);
        if (isNew) {
            messagingTemplate.convertAndSend("/topic/albuns", salvo);
        }
        return salvo;
    }
}
