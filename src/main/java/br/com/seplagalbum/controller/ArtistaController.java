package br.com.seplagalbum.controller;

import br.com.seplagalbum.dto.ArtistaRequest;
import br.com.seplagalbum.dto.ArtistaResponse;
import br.com.seplagalbum.mapper.ArtistaMapper;
import br.com.seplagalbum.model.Artista;
import br.com.seplagalbum.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/artistas")
@RequiredArgsConstructor
@Tag(name = "Artistas", description = "Endpoints para gerenciamento de artistas")
public class ArtistaController {

    private final ArtistaService service;
    private final ArtistaMapper mapper;

    @Operation(summary = "Listar artistas", description = "Retorna uma lista de artistas, permitindo busca por nome e ordenação")
    @GetMapping
    public ResponseEntity<CollectionModel<ArtistaResponse>> listar(
            @Parameter(description = "Nome do artista para busca parcial") @RequestParam(required = false) String nome,
            @Parameter(description = "Ordem alfabética (asc/desc)") @RequestParam(defaultValue = "asc") String ordem) {
        List<ArtistaResponse> artistas = service.listar(nome, ordem).stream()
                .map(mapper::toResponse)
                .map(this::addArtistaLinks)
                .collect(Collectors.toList());

        CollectionModel<ArtistaResponse> collection = CollectionModel.of(artistas,
                linkTo(methodOn(ArtistaController.class).listar(nome, ordem)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar artista por ID", description = "Retorna um artista pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista encontrado"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponse> buscarPorId(
            @Parameter(description = "ID do artista") @PathVariable Long id) {
        Artista artista = service.buscarPorId(id);
        ArtistaResponse response = addArtistaLinks(mapper.toResponse(artista));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Criar novo artista", description = "Cadastra um novo artista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artista criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ArtistaResponse> criar(@Valid @RequestBody ArtistaRequest request) {
        Artista artista = mapper.toEntity(request);
        Artista salvo = service.salvar(artista);
        ArtistaResponse response = addArtistaLinks(mapper.toResponse(salvo));
        return ResponseEntity.created(
                linkTo(methodOn(ArtistaController.class).buscarPorId(salvo.getId())).toUri()
        ).body(response);
    }

    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ArtistaResponse> atualizar(
            @Parameter(description = "ID do artista") @PathVariable Long id,
            @Valid @RequestBody ArtistaRequest request) {
        Artista existente = service.buscarPorId(id);
        mapper.updateEntity(request, existente);
        Artista atualizado = service.salvar(existente);
        ArtistaResponse response = addArtistaLinks(mapper.toResponse(atualizado));
        return ResponseEntity.ok(response);
    }

    private ArtistaResponse addArtistaLinks(ArtistaResponse response) {
        response.add(linkTo(methodOn(ArtistaController.class).buscarPorId(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(ArtistaController.class).listar(null, "asc")).withRel("artistas"));
        return response;
    }
}
