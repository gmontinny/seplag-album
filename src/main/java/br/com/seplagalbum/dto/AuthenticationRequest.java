package br.com.seplagalbum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Requisição de autenticação")
public class AuthenticationRequest {
    @Schema(description = "Nome de usuário", example = "admin")
    private String username;
    @Schema(description = "Senha do usuário", example = "password")
    private String password;
}
