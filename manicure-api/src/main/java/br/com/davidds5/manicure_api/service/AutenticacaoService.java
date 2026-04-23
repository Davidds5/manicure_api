package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    ClientRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
        return repository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
    
    



    
}