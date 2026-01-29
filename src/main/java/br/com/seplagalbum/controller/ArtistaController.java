package br.com.seplagalbum.controller;

import br.com.seplagalbum.model.Artista;
import br.com.seplagalbum.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artistas")
@RequiredArgsConstructor
@Tag(name = "Artistas", description = "Endpoints para gerenciamento de artistas")
public class ArtistaController {

    private final ArtistaService service;

    @Operation(summary = "Listar artistas", description = "Retorna uma lista de artistas, permitindo busca por nome e ordenação")
    @GetMapping
    public ResponseEntity<List<Artista>> listar(
            @Parameter(description = "Nome do artista para busca parcial") @RequestParam(required = false) String nome,
            @Parameter(description = "Ordem alfabética (asc/desc)") @RequestParam(defaultValue = "asc") String ordem) {
        return ResponseEntity.ok(service.listar(nome, ordem));
    }

    @Operation(summary = "Criar novo artista", description = "Cadastra um novo artista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Não autorizado")
    })
    @PostMapping
    public ResponseEntity<Artista> criar(@RequestBody Artista artista) {
        artista.setId(null);
        return ResponseEntity.ok(service.salvar(artista));
    }

    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Artista> atualizar(
            @Parameter(description = "ID do artista") @PathVariable Long id,
            @RequestBody Artista artista) {
        Artista existente = service.buscarPorId(id);
        artista.setId(existente.getId());
        return ResponseEntity.ok(service.salvar(artista));
    }
}
