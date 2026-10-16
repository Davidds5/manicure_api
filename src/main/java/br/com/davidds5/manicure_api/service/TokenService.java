package br.com.davidds5.manicure_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
    

    public String gerarToken(UserDetails usuario){
        try{
            Algorithm algorithms = Algorithm.HMAC256(secret);
            
            Long tenantId = null;
            if (usuario instanceof br.com.davidds5.manicure_api.entity.ProfessionalEntity professional) {
                tenantId = professional.getTenantId();
            } else if (usuario instanceof br.com.davidds5.manicure_api.entity.ClientEntity client) {
                tenantId = client.getTenantId();
            }

            var builder = JWT.create()
                .withIssuer("manicure_api")
                .withSubject(usuario.getUsername())
                .withClaim("role", usuario.getAuthorities().iterator().next().getAuthority())
                .withExpiresAt(dataExpiracao());

            if (tenantId != null) {
                builder.withClaim("tenant_id", tenantId);
            }

            return builder.sign(algorithms);
        }catch(JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }
    private Instant dataExpiracao(){
        return Instant.now().plus(2, java.time.temporal.ChronoUnit.HOURS);
    }

    public com.auth0.jwt.interfaces.DecodedJWT decodeToken(String tokenJWT) {
        try {
            Algorithm algorithms = Algorithm.HMAC256(secret);
            return JWT.require(algorithms)
                    .withIssuer("manicure_api")
                    .build()
                    .verify(tokenJWT);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
    }

    public String getRole(String tokenJWT){
        return decodeToken(tokenJWT).getClaim("role").asString();
    }

    public Long getTenantId(String tokenJWT){
        var claim = decodeToken(tokenJWT).getClaim("tenant_id");
        return claim.isNull() ? null : claim.asLong();
    }

    public String getSubject(String tokenJWT){
        return decodeToken(tokenJWT).getSubject();
    }
}