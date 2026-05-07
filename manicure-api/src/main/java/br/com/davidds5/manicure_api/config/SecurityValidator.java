package br.com.davidds5.manicure_api.config;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.repository.ClientRepository;

@Component("securityValidator")
public class SecurityValidator {

    private final ClientRepository clientRepository;
   
    public SecurityValidator(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public boolean isSelfOrAdmin(Long requestedClientId, Authentication authentication){

        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return true;
        }
        
        String loggerEmail = authentication.getName();
        
        Optional<ClientEntity> requestedClient = clientRepository.findById(requestedClientId);
        
        return requestedClient.isPresent() && requestedClient.get()
            .getEmail()
            .equals(loggerEmail);
    }
            
}