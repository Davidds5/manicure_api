package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    ProfessionalRepository professionalRepository;

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
    
        Optional<ClientEntity> client = clientRepository.findByEmail(username);
        if(client.isPresent()){
            return (UserDetails) client.get();
        }

        Optional<ProfessionalEntity> professional = professionalRepository.findByEmail(username);
        if(professional.isPresent()){
            return (UserDetails) professional.get();
        }
        throw new UsernameNotFoundException("Usuário não encontrado");
    }

    
    



    
}