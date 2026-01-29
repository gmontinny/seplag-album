package br.com.seplagalbum.controller;

import br.com.seplagalbum.model.Regional;
import br.com.seplagalbum.repository.RegionalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
@Tag(name = "Regionais", description = "Endpoints para consulta de regionais importadas")
public class RegionalController {

    private final RegionalRepository repository;

    @Operation(summary = "Listar regionais", description = "Retorna a lista de regionais armazenadas internamente, permitindo filtrar apenas por ativas")
    @GetMapping
    public ResponseEntity<List<Regional>> listar(
            @Parameter(description = "Filtrar apenas regionais ativas") 
            @RequestParam(required = false, defaultValue = "false") boolean apenasAtivas) {
        if (apenasAtivas) {
            return ResponseEntity.ok(repository.findByAtivoTrue());
        }
        return ResponseEntity.ok(repository.findAll());
    }
}
