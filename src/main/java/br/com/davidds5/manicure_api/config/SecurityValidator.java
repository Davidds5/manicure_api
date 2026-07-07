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

    public boolean isAppointmentOwnerOrAdmin(Long appointmentId, Authentication authentication) {
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return true;
        }
        
        String loggedEmail = authentication.getName();
        
        Optional<AppointmentEntity> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return true; 
        }
        
        AppointmentEntity appointment = appointmentOpt.get();
        return appointment.getClient() != null && appointment.getClient().getEmail().equals(loggedEmail);
    }
            
}