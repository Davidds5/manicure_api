package br.com.davidds5.manicure_api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.davidds5.manicure_api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var tokenJWT = recuperarToken(request);
                if(tokenJWT != null){
                    var subject = tokenService.getSubject(tokenJWT);
                    var role = tokenService.getRole(tokenJWT);

                    var authority = new SimpleGrantedAuthority(role);

                    var authentication = new UsernamePasswordAuthenticationToken(subject, null, List.of(authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

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