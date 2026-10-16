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
    private ClientRepository clientRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long tenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();

        if (tenantId != null) {
            // Check professional first within tenant
            Optional<ProfessionalEntity> professional = professionalRepository.findByEmailAndTenantId(username, tenantId);
            if (professional.isPresent()) {
                return (UserDetails) professional.get();
            }

            // Check client within tenant
            Optional<ClientEntity> client = clientRepository.findByEmailAndTenantId(username, tenantId);
            if (client.isPresent()) {
                return (UserDetails) client.get();
            }
        } else {
            // Global login: only SUPER_ADMIN is allowed without tenant context
            Optional<ProfessionalEntity> superAdmin = professionalRepository.findByEmailAndRole(username, br.com.davidds5.manicure_api.enums.ProfessionalRole.SUPER_ADMIN);
            if (superAdmin.isPresent()) {
                return (UserDetails) superAdmin.get();
            }
        }

        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}