package br.com.seplagalbum.dto;

import br.com.seplagalbum.model.Artista;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Resposta com dados do artista")
public class ArtistaResponse extends RepresentationModel<ArtistaResponse> {

    @Schema(description = "ID do artista", example = "1")
    private Long id;

    @Schema(description = "Nome do artista", example = "Serj Tankian")
    private String nome;

    @Schema(description = "Tipo de artista", example = "CANTOR")
    private Artista.TipoArtista tipo;
}
