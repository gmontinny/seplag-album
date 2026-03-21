package br.com.seplagalbum.controller;

import br.com.seplagalbum.dto.RegionalResponse;
import br.com.seplagalbum.model.Regional;
import br.com.seplagalbum.service.RegionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
@Tag(name = "Regionais", description = "Endpoints para consulta de regionais importadas")
public class RegionalController {

    private final RegionalService service;

    @Operation(summary = "Listar regionais", description = "Retorna a lista de regionais armazenadas internamente, permitindo filtrar apenas por ativas")
    @GetMapping
    public ResponseEntity<CollectionModel<RegionalResponse>> listar(
            @Parameter(description = "Filtrar apenas regionais ativas")
            @RequestParam(required = false, defaultValue = "false") boolean apenasAtivas) {
        List<RegionalResponse> content = service.listar(apenasAtivas).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        CollectionModel<RegionalResponse> collection = CollectionModel.of(content,
                linkTo(methodOn(RegionalController.class).listar(apenasAtivas)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar regional por ID interno", description = "Retorna uma regional pelo seu ID interno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Regional encontrada"),
            @ApiResponse(responseCode = "404", description = "Regional não encontrada")
    })
    @GetMapping("/{internalId}")
    public ResponseEntity<RegionalResponse> buscarPorId(
            @Parameter(description = "ID interno da regional") @PathVariable Long internalId) {
        Regional regional = service.buscarPorInternalId(internalId);
        return ResponseEntity.ok(toResponse(regional));
    }

    private RegionalResponse toResponse(Regional regional) {
        RegionalResponse response = new RegionalResponse(
                regional.getInternalId(), regional.getId(), regional.getNome(), regional.getAtivo());
        response.add(linkTo(methodOn(RegionalController.class).buscarPorId(regional.getInternalId())).withSelfRel());
        response.add(linkTo(methodOn(RegionalController.class).listar(false)).withRel("regionais"));
        return response;
    }
}
