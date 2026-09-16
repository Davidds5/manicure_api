package br.com.davidds5.manicure_api.config;

import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityValidatorTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private SecurityValidator securityValidator;

    @BeforeEach
    void setUp() {
        securityValidator = new SecurityValidator(clientRepository, appointmentRepository);
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Deve negar acesso (fail-closed) quando agendamento não for encontrado")
    void shouldDenyAccessWhenAppointmentNotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean allowed = securityValidator.isAppointmentOwnerOrAdmin(99L, auth);

        assertFalse(allowed, "Deve retornar false se o agendamento não existir no banco/tenant");
    }

    @Test
    @DisplayName("Deve negar acesso quando agendamento pertencer a outro tenant, mesmo para ROLE_ADMIN")
    void shouldDenyAdminFromDifferentTenant() {
        TenantContext.setTenantId(1L);
        AppointmentEntity appointment = AppointmentEntity.builder()
                .id(10L)
                .tenantId(2L) // Outro tenant!
                .build();
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        Authentication adminAuth = new UsernamePasswordAuthenticationToken("admin@tenant1.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        boolean allowed = securityValidator.isAppointmentOwnerOrAdmin(10L, adminAuth);

        assertFalse(allowed, "Admin do Tenant 1 não pode acessar agendamento do Tenant 2");
    }

    @Test
    @DisplayName("Deve permitir acesso para ROLE_ADMIN quando agendamento for do mesmo tenant")
    void shouldAllowAdminWithinSameTenant() {
        TenantContext.setTenantId(1L);
        AppointmentEntity appointment = AppointmentEntity.builder()
                .id(10L)
                .tenantId(1L)
                .build();
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        Authentication adminAuth = new UsernamePasswordAuthenticationToken("admin@tenant1.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        boolean allowed = securityValidator.isAppointmentOwnerOrAdmin(10L, adminAuth);

        assertTrue(allowed, "Admin do mesmo tenant deve ter acesso permitido");
    }

    @Test
    @DisplayName("Deve permitir acesso para ROLE_USER se for o dono do agendamento no mesmo tenant")
    void shouldAllowOwnerClientWithinSameTenant() {
        TenantContext.setTenantId(1L);
        ClientEntity client = ClientEntity.builder()
                .id(5L)
                .email("cliente@test.com")
                .tenantId(1L)
                .build();
        AppointmentEntity appointment = AppointmentEntity.builder()
                .id(10L)
                .tenantId(1L)
                .client(client)
                .build();
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        Authentication userAuth = new UsernamePasswordAuthenticationToken("cliente@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean allowed = securityValidator.isAppointmentOwnerOrAdmin(10L, userAuth);

        assertTrue(allowed, "Cliente dono do agendamento deve ter acesso");
    }

    @Test
    @DisplayName("Deve negar acesso para ROLE_USER se não for o dono do agendamento")
    void shouldDenyNonOwnerClient() {
        TenantContext.setTenantId(1L);
        ClientEntity client = ClientEntity.builder()
                .id(5L)
                .email("outro@test.com")
                .tenantId(1L)
                .build();
        AppointmentEntity appointment = AppointmentEntity.builder()
                .id(10L)
                .tenantId(1L)
                .client(client)
                .build();
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        Authentication userAuth = new UsernamePasswordAuthenticationToken("intruso@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean allowed = securityValidator.isAppointmentOwnerOrAdmin(10L, userAuth);

        assertFalse(allowed, "Cliente que não é dono do agendamento deve ter acesso negado");
    }

    @Test
    @DisplayName("Deve negar isSelfOrAdmin quando cliente não for encontrado")
    void shouldDenySelfWhenClientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        boolean allowed = securityValidator.isSelfOrAdmin(99L, auth);

        assertFalse(allowed, "Deve retornar false se cliente não existir");
    }

    @Test
    @DisplayName("Deve negar isSelfOrAdmin para Admin de outro tenant")
    void shouldDenySelfOrAdminForAdminOfOtherTenant() {
        TenantContext.setTenantId(1L);
        ClientEntity client = ClientEntity.builder()
                .id(5L)
                .email("client@tenant2.com")
                .tenantId(2L)
                .build();
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));

        Authentication adminAuth = new UsernamePasswordAuthenticationToken("admin@tenant1.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        boolean allowed = securityValidator.isSelfOrAdmin(5L, adminAuth);

        assertFalse(allowed, "Admin do Tenant 1 não pode acessar dados de cliente do Tenant 2");
    }
}
