package br.com.davidds5.manicure_api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.davidds5.manicure_api.service.TokenService;
import br.com.davidds5.manicure_api.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var tokenJWT = recuperarToken(request);
                if(tokenJWT != null){
                    var subject = tokenService.getSubject(tokenJWT);

                    try{

                        UserDetails usuario = autenticacaoService.loadUserByUsername(subject);
                        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    }catch(UsernameNotFoundException exception){
                        
                    }
                    
                }

                filterChain.doFilter(request, response);
                
}
    private String recuperarToken(HttpServletRequest request){
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null){
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}