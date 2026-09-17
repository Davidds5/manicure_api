package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.data.AppointmentData;
import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.entity.*;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.ServiceRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Spy
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();
 

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ProfessionalRepository professionalRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private AppointmentData appointmentData;

    @InjectMocks
    private AppointmentService appointmentService;

    private ClientEntity client;
    private ProfessionalEntity professional;
    private ServiceEntity serviceEntity;
    private AppointmentCreateDTO createDTO;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        br.com.davidds5.manicure_api.config.TenantContext.clear();
    }

    @BeforeEach
    void setUp() {
        br.com.davidds5.manicure_api.config.TenantContext.setTenantId(1L);

        client = new ClientEntity();
        client.setId(1L);
        client.setTenantId(1L);
        client.setName("Maria");
        client.setEmail("maria@test.com");

        professional = new ProfessionalEntity();
        professional.setId(1L);
        professional.setTenantId(1L);
        professional.setName("Ana");
        professional.setActive(true);
        professional.setEmail("ana@email.com");
        professional.setPassword("123456789");
        professional.setSpecialty("Nail Designer");

        serviceEntity = new ServiceEntity();
        serviceEntity.setId(1L);
        serviceEntity.setTenantId(1L);
        serviceEntity.setName("Manicure Simples");
        serviceEntity.setPrice(50.0);

        createDTO = new AppointmentCreateDTO();
        createDTO.setClientId(1L);
        createDTO.setProfessionalId(1L);
        createDTO.setServiceId(1L);
        createDTO.setDateTime(LocalDateTime.now().plusDays(1)); // Instante no futuro
    }

    @Test
    void createAppointment_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(appointmentData.findByProfessionalIdAndDateTime(anyLong(), any())).thenReturn(Collections.emptyList());

        AppointmentEntity savedEntity = new AppointmentEntity();
        savedEntity.setId(100L);
        savedEntity.setTenantId(1L);
        savedEntity.setClient(client);
        savedEntity.setProfessional(professional);
        savedEntity.setService(serviceEntity);
        savedEntity.setDateTime(createDTO.getDateTime());
        savedEntity.setStatus(AppointmentEntity.AppointmentStatus.SCHEDULED);

        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(savedEntity);

        AppointmentDTO result = appointmentService.createAppointment(createDTO);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Maria", result.getClientName());
        assertEquals(AppointmentEntity.AppointmentStatus.SCHEDULED, result.getStatus());

        verify(appointmentRepository).save(any(AppointmentEntity.class));
    }

    @Test
    void createAppointment_CrossTenantClient_ThrowsResourceNotFoundException() {
        client.setTenantId(2L); // Pertence a outro tenant
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThrows(br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException.class, 
                () -> appointmentService.createAppointment(createDTO));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_CrossTenantProfessional_ThrowsResourceNotFoundException() {
        professional.setTenantId(2L); // Pertence a outro tenant
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));

        assertThrows(br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException.class, 
                () -> appointmentService.createAppointment(createDTO));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_CrossTenantService_ThrowsResourceNotFoundException() {
        serviceEntity.setTenantId(2L); // Pertence a outro tenant
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        assertThrows(br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException.class, 
                () -> appointmentService.createAppointment(createDTO));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_ProfessionalInactive_ThrowsBusinessException() {
        professional.setActive(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));

        BusinessException ex = assertThrows(BusinessException.class, () -> appointmentService.createAppointment(createDTO));
        assertEquals("Profissional inativo", ex.getMessage());

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_PastDate_ThrowsBusinessException() {
        createDTO.setDateTime(LocalDateTime.now().minusDays(1));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        assertThrows(BusinessException.class, () -> appointmentService.createAppointment(createDTO));

        verify(appointmentRepository, never()).save(any());
    }
}
