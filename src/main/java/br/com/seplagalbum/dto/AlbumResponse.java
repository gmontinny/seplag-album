package br.com.seplagalbum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta com dados do álbum")
public class AlbumResponse {

    @Schema(description = "ID do álbum", example = "1")
    private Long id;

    @Schema(description = "Título do álbum", example = "Harakiri")
    private String titulo;

    @Schema(description = "URL da capa do álbum")
    private String capaUrl;

    @Schema(description = "Artistas do álbum")
    private Set<ArtistaResponse> artistas;
}
