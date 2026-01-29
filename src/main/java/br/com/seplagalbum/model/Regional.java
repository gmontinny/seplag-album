package br.com.seplagalbum.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Regional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    private Long internalId;

    @Column(nullable = false)
    @io.swagger.v3.oas.annotations.media.Schema(description = "ID da regional vindo do integrador", example = "1")
    private Integer id;

    @Column(length = 200, nullable = false)
    @io.swagger.v3.oas.annotations.media.Schema(description = "Nome da regional", example = "Regional Norte")
    private String nome;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Indica se a regional está ativa")
    private Boolean ativo = true;
}
