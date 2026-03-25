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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

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

    @BeforeEach
    void setUp() {
        client = new ClientEntity();
        client.setId(1L);
        client.setName("Maria");

        professional = new ProfessionalEntity();
        professional.setId(1L);
        professional.setName("Ana");
        professional.setActive(true);

        serviceEntity = new ServiceEntity();
        serviceEntity.setId(1L);
        serviceEntity.setName("Manicure Simples");

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
