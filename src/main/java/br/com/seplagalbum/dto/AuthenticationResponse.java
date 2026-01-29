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
@Schema(description = "Resposta de autenticação contendo o token JWT")
public class AuthenticationResponse {
    @Schema(description = "Token JWT para acesso aos endpoints protegidos")
    private String token;
}
