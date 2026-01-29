package br.com.seplagalbum.service;

import br.com.seplagalbum.dto.AuthenticationRequest;
import br.com.seplagalbum.dto.AuthenticationResponse;
import br.com.seplagalbum.repository.UsuarioRepository;
import br.com.seplagalbum.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Tentando autenticar usuário: {}", request.getUsername());
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            log.info("Autenticação bem-sucedida para: {}", request.getUsername());
        } catch (Exception e) {
            log.error("Erro na autenticação para usuário {}: {}", request.getUsername(), e.getMessage());
            throw e;
        }

        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado após autenticação: {}", request.getUsername());
                    return new RuntimeException("Usuário não encontrado");
                });
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse refreshToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            var jwtToken = jwtService.generateToken(userDetails);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        throw new RuntimeException("Não autorizado para renovação");
    }
}
