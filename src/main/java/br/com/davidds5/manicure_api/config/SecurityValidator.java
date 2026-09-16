package br.com.davidds5.manicure_api.config;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;

@Component("securityValidator")
public class SecurityValidator {

    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
   
    public SecurityValidator(ClientRepository clientRepository, AppointmentRepository appointmentRepository){
        this.clientRepository = clientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public boolean isSelfOrAdmin(Long requestedClientId, Authentication authentication) {
        if (requestedClientId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<ClientEntity> requestedClient = clientRepository.findById(requestedClientId);
        if (requestedClient.isEmpty()) {
            return false;
        }

        ClientEntity client = requestedClient.get();
        Long currentTenantId = TenantContext.getTenantId();
        if (currentTenantId != null && !currentTenantId.equals(client.getTenantId())) {
            return false;
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return true;
        }

        String loggedEmail = authentication.getName();
        return client.getEmail() != null && client.getEmail().equals(loggedEmail);
    }

    public boolean isAppointmentOwnerOrAdmin(Long appointmentId, Authentication authentication) {
        if (appointmentId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<AppointmentEntity> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }

        AppointmentEntity appointment = appointmentOpt.get();
        Long currentTenantId = TenantContext.getTenantId();
        if (currentTenantId != null && !currentTenantId.equals(appointment.getTenantId())) {
            return false;
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return true;
        }

        String loggedEmail = authentication.getName();
        return appointment.getClient() != null 
                && appointment.getClient().getEmail() != null 
                && appointment.getClient().getEmail().equals(loggedEmail);
    }
}