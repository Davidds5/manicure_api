package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ServiceUpdateDTO;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.repository.ServiceRepository;
import br.com.davidds5.manicure_api.mapper.ServiceMapper;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest{

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceCreateDTO createDTO;
    private ServiceEntity entity;  
    private ServiceDTO dto;

    @BeforeEach
    void setUp() {

        createDTO = new ServiceCreateDTO();
        createDTO.setName("Manicure");
        createDTO.setDescription("Manicure completa");
        createDTO.setPrice(50.0);
        createDTO.setDuration(60);
        createDTO.setActive(true);

        entity = new ServiceEntity();
        entity.setId(1L);
        entity.setName("Manicure");
        entity.setDescription("Manicure completa");
        entity.setPrice(50.0);
        entity.setDuration(60);
        entity.setActive(true);

        dto = new ServiceDTO();
        dto.setId(1L);
        dto.setName("Manicure");
        dto.setDescription("Manicure completa");
        dto.setPrice(50.0);
        dto.setDuration(60);
        dto.setActive(true);


        
    }

    @Test
    void createService_Success() {
        when(serviceMapper.toEntity(createDTO)).thenReturn(entity);
        when(serviceRepository.save(entity)).thenReturn(entity);
        when(serviceMapper.toDTO(entity)).thenReturn(dto);

        ServiceDTO result = serviceService.createService(createDTO);

        assertNotNull(result);
        assertEquals("Manicure", result.getName());

        verify(serviceMapper).toEntity(createDTO);
        verify(serviceRepository).save(entity);
        verify(serviceMapper).toDTO(entity);
    }
    
    @Test
    void createService_ShouldThrowException_WhenPriceIsInvalid() {
        
        // Arrange
        createDTO.setPrice(-20.0);

        // Act
        BusinessException exception = assertThrows(BusinessException.class, () -> serviceService.createService(createDTO));

        // Assert
        assertEquals("Preço deve ser maior que zero", exception.getMessage());

        verify(serviceRepository, never()).save(any());

    }

    @Test
    void findById_ShouldThowException_WhenIdIsInvalid() {

        Long id = 1L;

        when(serviceRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()-> serviceService.findById(id));

        assertEquals("Serviço não encontrado com ID: " + id, exception.getMessage());

        verify(serviceRepository).findById(id);
        verifyNoInteractions(serviceMapper);
        
    }   

    @Test
    void findById_Success() {
        Long id = 1L;

        when(serviceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(serviceMapper.toDTO(entity)).thenReturn(dto);

        ServiceDTO result = serviceService.findById(id);

        assertNotNull(result);
        assertEquals("Manicure", result.getName());

        verify(serviceRepository).findById(id);
        verify(serviceMapper).toDTO(entity);
    }

    @Test
    void delete_Success() {
        Long id = 1L;
        when(serviceRepository.findById(id)).thenReturn(Optional.of(entity));

        serviceService.deleteService(id);

        verify(serviceRepository, times(1)).delete(entity);
        
    }

    @Test
    void delete_Error(){
        Long id = 1L;

        when(serviceRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> serviceService.deleteService(id));

        verify(serviceRepository, never()).delete(entity);
    }

    @Test
    void update_Success() {
        ServiceUpdateDTO updated = new ServiceUpdateDTO();
        updated.setName("Mao do pe");
        updated.setDescription("Feito co rapides");
        updated.setPrice(40.0);
        updated.setDuration(20);
        updated.setActive(true);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(entity);
        when(serviceMapper.toDTO(any(ServiceEntity.class))).thenReturn(dto);

        serviceService.updateService(1L, updated);

        ArgumentCaptor<ServiceEntity> entityCaptor = ArgumentCaptor.forClass(ServiceEntity.class);
        verify(serviceRepository).save(entityCaptor.capture());
      
        ServiceEntity saved = entityCaptor.getValue();
        assertEquals(saved.getName(), updated.getName());
        assertEquals(saved.getDescription(), updated.getDescription());
        assertEquals(saved.getPrice(), updated.getPrice());
        assertEquals(saved.getDuration(), updated.getDuration());
        assertEquals(saved.getActive(), updated.getActive());
 
    }
}