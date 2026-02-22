package br.com.seplagalbum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação/atualização de álbum")
public class AlbumRequest {

    @NotBlank(message = "{album.titulo.notblank}")
    @Size(min = 1, max = 200, message = "{album.titulo.size}")
    @Schema(description = "Título do álbum", example = "Harakiri", required = true)
    private String titulo;

    @Schema(description = "IDs dos artistas do álbum", example = "[1, 2]")
    private Set<Long> artistaIds;
}
