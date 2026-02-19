package br.com.seplagalbum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação/atualização de álbum")
public class AlbumRequest {

    @NotBlank(message = "Título do álbum é obrigatório")
    @Schema(description = "Título do álbum", example = "Harakiri", required = true)
    private String titulo;

    @Schema(description = "IDs dos artistas do álbum", example = "[1, 2]")
    private Set<Long> artistaIds;
}
