package br.com.seplagalbum.controller;

import br.com.seplagalbum.dto.AlbumRequest;
import br.com.seplagalbum.dto.AlbumResponse;
import br.com.seplagalbum.mapper.AlbumMapper;
import br.com.seplagalbum.model.Album;
import br.com.seplagalbum.model.Artista;
import br.com.seplagalbum.service.AlbumService;
import br.com.seplagalbum.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/albuns")
@RequiredArgsConstructor
@Tag(name = "Álbuns", description = "Endpoints para gerenciamento de álbuns")
public class AlbumController {

    private final AlbumService service;
    private final StorageService storageService;
    private final AlbumMapper mapper;

    @Operation(summary = "Listar álbuns", description = "Retorna uma página de álbuns, permitindo filtrar por tipo de artista")
    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> listar(
            @Parameter(description = "Tipo de artista (CANTOR/BANDA)") @RequestParam(required = false) Artista.TipoArtista tipo,
            @Parameter(description = "Número da página", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Album> albuns = service.listar(tipo, pageable);
        Page<AlbumResponse> response = albuns.map(album -> {
            album.setCapaUrl(storageService.getPresignedUrl(album.getCapaUrl()));
            return mapper.toResponse(album);
        });
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Criar novo álbum", description = "Cadastra um novo álbum no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum criado com sucesso")
    })
    @PostMapping
    public ResponseEntity<AlbumResponse> criar(@Valid @RequestBody AlbumRequest request) {
        Album album = mapper.toEntity(request);
        Album salvo = service.salvar(album);
        return ResponseEntity.ok(mapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar álbum", description = "Atualiza os dados de um álbum existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> atualizar(
            @Parameter(description = "ID do álbum") @PathVariable Long id,
            @Valid @RequestBody AlbumRequest request) {
        Album existente = service.buscarPorId(id);
        mapper.updateEntity(request, existente);
        Album atualizado = service.salvar(existente);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Operation(summary = "Upload de capa", description = "Realiza o upload da imagem de capa para um álbum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso")
    })
    @PostMapping(value = "/{id}/capa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponse> uploadCapa(
            @Parameter(description = "ID do álbum") @PathVariable Long id,
            @Parameter(description = "Arquivo da imagem de capa", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestParam("file") MultipartFile file) {
        Album album = service.buscarPorId(id);
        String fileName = storageService.uploadImage(file);
        album.setCapaUrl(fileName);
        Album atualizado = service.salvar(album);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }
}
