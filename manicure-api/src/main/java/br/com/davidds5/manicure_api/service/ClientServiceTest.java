package br.com.davidds5.manicure_api.service;


import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static javax.management.Query.times;
import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private ClientCreateDTO createDTO;
    private ClientEntity entity;
    private ClientDTO dto;

    @BeforeEach
    void setUp() {
        createDTO = ClientCreatedDTO.builder()
                .name("Maria Silva")
                .phone("(11) 99999-9999")
                .email("maria@email.com")
                .build();

        entity = ClientEntity.builder()
                .id(1L)
                .name("Maria Silva")
                .phone("(11) 99999-9999")
                .email("maria@email.com")
                .build();

        dto = ClientDTO.builder()
                .id(1L)
                .name("Maria Silva")
                .phone("(11) 99999-9999")
                .email("maria@email.com")
                .build();
    }

    @Test
    void createClient_Success() {
        // Arrange
        when(clientRepository.findByEmail(createDTO.getEmail())).thenReturn(Optional.empty());
        when(clientMapper.toEntity(createDTO)).thenReturn(entity);
        when(clientRepository.save(entity)).thenReturn(entity);
        when(clientMapper.toDTO(entity)).thenReturn(dto);

        // Act
        ClientDTO result = clientService.createClient(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Maria Silva", result.getName());
        verify(clientRepository, times(1)).save(any(ClientEntity.class));
    }

    @Test
    void createClient_EmailAlreadyExists_ThrowsBusinessException() {
        // Arrange
        when(clientRepository.findByEmail(createDTO.getEmail())).thenReturn(Optional.of(entity));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> clientService.createClient(createDTO));
        assertEquals("Email já cadastrado: maria@email.com", exception.getMessage());
    }

    @Test
    void findById_ClientNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> clientService.findById(999L));
        assertEquals("Cliente não encontrado com ID: 999", exception.getMessage());
    }
}