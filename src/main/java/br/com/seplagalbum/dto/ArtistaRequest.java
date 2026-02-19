package br.com.seplagalbum.dto;

import br.com.seplagalbum.model.Artista;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação/atualização de artista")
public class ArtistaRequest {

    @NotBlank(message = "Nome do artista é obrigatório")
    @Schema(description = "Nome do artista", example = "Serj Tankian", required = true)
    private String nome;

    @NotNull(message = "Tipo do artista é obrigatório")
    @Schema(description = "Tipo de artista", example = "CANTOR", required = true)
    private Artista.TipoArtista tipo;
}
