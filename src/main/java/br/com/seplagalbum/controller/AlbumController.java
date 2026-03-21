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
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
    public ResponseEntity<PagedModel<AlbumResponse>> listar(
            @Parameter(description = "Tipo de artista (CANTOR/BANDA)") @RequestParam(required = false) Artista.TipoArtista tipo,
            @Parameter(description = "Número da página", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Album> albuns = service.listar(tipo, pageable);

        List<AlbumResponse> content = albuns.getContent().stream()
                .map(album -> {
                    album.setCapaUrl(storageService.getPresignedUrl(album.getCapaUrl()));
                    return addAlbumLinks(mapper.toResponse(album));
                })
                .collect(Collectors.toList());

        PageMetadata metadata = new PageMetadata(albuns.getSize(), albuns.getNumber(), albuns.getTotalElements(), albuns.getTotalPages());
        PagedModel<AlbumResponse> pagedModel = PagedModel.of(content, metadata,
                linkTo(methodOn(AlbumController.class).listar(tipo, page, size)).withSelfRel());

        if (albuns.hasNext()) {
            pagedModel.add(linkTo(methodOn(AlbumController.class).listar(tipo, page + 1, size)).withRel("next"));
        }
        if (albuns.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(AlbumController.class).listar(tipo, page - 1, size)).withRel("prev"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar álbum por ID", description = "Retorna um álbum pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum encontrado"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> buscarPorId(
            @Parameter(description = "ID do álbum") @PathVariable Long id) {
        Album album = service.buscarPorId(id);
        album.setCapaUrl(storageService.getPresignedUrl(album.getCapaUrl()));
        AlbumResponse response = addAlbumLinks(mapper.toResponse(album));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Criar novo álbum", description = "Cadastra um novo álbum no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Álbum criado com sucesso")
    })
    @PostMapping
    public ResponseEntity<AlbumResponse> criar(@Valid @RequestBody AlbumRequest request) {
        Album album = mapper.toEntity(request);
        Album salvo = service.salvar(album);
        AlbumResponse response = addAlbumLinks(mapper.toResponse(salvo));
        return ResponseEntity.created(
                linkTo(methodOn(AlbumController.class).buscarPorId(salvo.getId())).toUri()
        ).body(response);
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
        AlbumResponse response = addAlbumLinks(mapper.toResponse(atualizado));
        return ResponseEntity.ok(response);
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
        AlbumResponse response = addAlbumLinks(mapper.toResponse(atualizado));
        return ResponseEntity.ok(response);
    }

    private AlbumResponse addAlbumLinks(AlbumResponse response) {
        response.add(linkTo(methodOn(AlbumController.class).buscarPorId(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(AlbumController.class).listar(null, 0, 10)).withRel("albuns"));
        response.add(linkTo(methodOn(AlbumController.class).uploadCapa(response.getId(), null)).withRel("upload-capa"));
        return response;
    }
}
