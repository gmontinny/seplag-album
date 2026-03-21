package br.com.seplagalbum.dto;

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
@Schema(description = "Resposta com dados da regional")
public class RegionalResponse extends RepresentationModel<RegionalResponse> {

    @Schema(description = "ID interno da regional", example = "1")
    private Long internalId;

    @Schema(description = "ID da regional vindo do integrador", example = "10")
    private Integer id;

    @Schema(description = "Nome da regional", example = "Regional Norte")
    private String nome;

    @Schema(description = "Indica se a regional está ativa")
    private Boolean ativo;
}
