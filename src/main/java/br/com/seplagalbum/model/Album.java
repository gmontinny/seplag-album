package br.com.seplagalbum.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um álbum")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do álbum", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Título do álbum", example = "Harakiri")
    private String titulo;

    @Schema(description = "URL da imagem de capa (armazenada no MinIO)", accessMode = Schema.AccessMode.READ_ONLY)
    private String capaUrl;

    @ManyToMany
    @JoinTable(
        name = "artista_album",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "artista_id")
    )
    @Schema(description = "Conjunto de artistas que participam do álbum")
    private Set<Artista> artistas = new HashSet<>();
}
