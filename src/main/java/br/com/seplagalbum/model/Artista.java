package br.com.seplagalbum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Schema(description = "Entidade que representa um artista")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do artista", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do artista", example = "Serj Tankian")
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo de artista (CANTOR ou BANDA)", example = "CANTOR")
    private TipoArtista tipo;

    @ManyToMany(mappedBy = "artistas")
    @JsonIgnore
    @Schema(hidden = true)
    private Set<Album> albuns = new HashSet<>();

    public enum TipoArtista {
        CANTOR, BANDA
    }
}
