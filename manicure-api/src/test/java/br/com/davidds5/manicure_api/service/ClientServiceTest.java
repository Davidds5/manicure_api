package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ClientMapper;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private ClientCreatedDTO createDTO;
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

    // ================= CREATE =================

    @Test
    void createClient_Success() {
        when(clientRepository.findByEmail(createDTO.getEmail())).thenReturn(Optional.empty());
        when(clientMapper.toEntity(createDTO)).thenReturn(entity);
        when(clientRepository.save(entity)).thenReturn(entity);
        when(clientMapper.toDTO(entity)).thenReturn(dto);

        ClientDTO result = clientService.createClient(createDTO);

        assertNotNull(result);
        assertEquals("Maria Silva", result.getName());

        verify(clientRepository).findByEmail(createDTO.getEmail());
        verify(clientMapper).toEntity(createDTO);
        verify(clientRepository).save(entity);
        verify(clientMapper).toDTO(entity);
    }

    @Test
    void createClient_EmailAlreadyExists_ThrowsBusinessException() {
        when(clientRepository.findByEmail(createDTO.getEmail())).thenReturn(Optional.of(entity));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.createClient(createDTO)
        );

        assertEquals("Email já cadastrado: " + createDTO.getEmail(), exception.getMessage());

        verify(clientRepository).findByEmail(createDTO.getEmail());
        verify(clientRepository, never()).save(any());
    }

    // ================= FIND =================

    @Test
    void findById_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(clientMapper.toDTO(entity)).thenReturn(dto);

        ClientDTO result = clientService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(clientRepository).findById(1L);
        verify(clientMapper).toDTO(entity);
    }

    @Test
    void findById_ClientNotFound_ThrowsResourceNotFoundException() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.findById(999L)
        );

        assertEquals("Cliente não encontrado com ID: 999", exception.getMessage());

        verify(clientRepository).findById(999L);
    }
}