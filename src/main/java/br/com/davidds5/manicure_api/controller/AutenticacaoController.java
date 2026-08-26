package br.com.davidds5.manicure_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.davidds5.manicure_api.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import br.com.davidds5.manicure_api.dto.AutenticacaoDTO;
import br.com.davidds5.manicure_api.dto.TokenDTO;


@RestController
@RequestMapping({"/login", "/auth/login"})
public class AutenticacaoController {
    
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid AutenticacaoDTO data){
        try {
            var token = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var autenticacao = manager.authenticate(token);

            var tokenJWT = tokenService.gerarToken((UserDetails) autenticacao.getPrincipal());
            return ResponseEntity.ok(new TokenDTO(tokenJWT));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "E-mail ou senha incorretos. Verifique suas credenciais."));
        }
    }

}