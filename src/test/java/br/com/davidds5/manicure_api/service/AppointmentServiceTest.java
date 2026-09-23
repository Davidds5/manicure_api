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
import java.util.List;
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

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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
        when(appointmentRepository.findActiveByProfessionalAndDateRange(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());

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

    @Test
    void createPublicAppointment_Success() {
        var publicDTO = br.com.davidds5.manicure_api.dto.PublicAppointmentCreateDTO.builder()
                .clientName("Carla Souza")
                .clientEmail("carla@email.com")
                .clientPhone("(11) 98888-7777")
                .professionalId(1L)
                .serviceId(1L)
                .dateTime(LocalDateTime.now().plusDays(2))
                .build();

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(clientRepository.findByEmailAndTenantId("carla@email.com", 1L)).thenReturn(Optional.empty());
        when(clientRepository.findByPhoneAndTenantId("(11) 98888-7777", 1L)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(clientRepository.save(any())).thenAnswer(invocation -> {
            ClientEntity c = invocation.getArgument(0);
            c.setId(10L);
            return c;
        });
        when(appointmentRepository.save(any())).thenAnswer(invocation -> {
            AppointmentEntity a = invocation.getArgument(0);
            a.setId(99L);
            return a;
        });

        AppointmentDTO result = appointmentService.createPublicAppointment(publicDTO);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Carla Souza", result.getClientName());
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    void createAppointment_TimeConflictOverlap_ThrowsBusinessException() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0);
        createDTO.setDateTime(appointmentTime);
        serviceEntity.setDuration(45); // 10:30 - 11:15

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        // Existing appointment: 10:00 to 11:00 (duration 60 min)
        ServiceEntity existingService = new ServiceEntity();
        existingService.setDuration(60);
        AppointmentEntity existing = AppointmentEntity.builder()
                .id(50L)
                .dateTime(appointmentTime.minusMinutes(30)) // 10:00
                .service(existingService)
                .status(AppointmentEntity.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.findActiveByProfessionalAndDateRange(anyLong(), any(), any(), any()))
                .thenReturn(List.of(existing));

        BusinessException ex = assertThrows(BusinessException.class, () -> appointmentService.createAppointment(createDTO));
        assertEquals(br.com.davidds5.manicure_api.util.Constants.TIME_CONFLICT, ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_CancelledAppointmentIgnored_Success() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        createDTO.setDateTime(appointmentTime);
        serviceEntity.setDuration(60);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        // Existing appointment at exact same time, but CANCELLED
        AppointmentEntity cancelledAppointment = AppointmentEntity.builder()
                .id(50L)
                .dateTime(appointmentTime)
                .service(serviceEntity)
                .status(AppointmentEntity.AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findActiveByProfessionalAndDateRange(anyLong(), any(), any(), any()))
                .thenReturn(List.of(cancelledAppointment));

        AppointmentEntity savedEntity = AppointmentEntity.builder()
                .id(101L)
                .tenantId(1L)
                .client(client)
                .professional(professional)
                .service(serviceEntity)
                .dateTime(appointmentTime)
                .status(AppointmentEntity.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(savedEntity);

        AppointmentDTO result = appointmentService.createAppointment(createDTO);
        assertNotNull(result);
        assertEquals(101L, result.getId());
        verify(appointmentRepository).save(any());
    }

    @Test
    void getOccupiedSlots_ReturnsActiveSlotsOnly() {
        java.time.LocalDate date = java.time.LocalDate.now().plusDays(2);
        LocalDateTime start1 = date.atTime(14, 0);
        LocalDateTime start2 = date.atTime(16, 0);

        ServiceEntity s1 = new ServiceEntity();
        s1.setDuration(60);

        AppointmentEntity a1 = AppointmentEntity.builder()
                .id(1L)
                .dateTime(start1)
                .service(s1)
                .status(AppointmentEntity.AppointmentStatus.SCHEDULED)
                .build();

        AppointmentEntity a2Cancelled = AppointmentEntity.builder()
                .id(2L)
                .dateTime(start2)
                .service(s1)
                .status(AppointmentEntity.AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findActiveByProfessionalAndDateRange(eq(1L), any(), any(), any()))
                .thenReturn(List.of(a1, a2Cancelled));

        var slots = appointmentService.getOccupiedSlots(1L, date);

        assertEquals(1, slots.size());
        assertEquals("14:00", slots.get(0).getTime());
        assertEquals("15:00", slots.get(0).getEndTime());
        assertEquals(60, slots.get(0).getDurationMinutes());
    }
}

