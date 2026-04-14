package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ProfessionalMapper;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private ProfessionalMapper professionalMapper;

    @InjectMocks
    private ProfessionalService professionalService;

    private ProfessionalCreatedDTO createDTO;
    private ProfessionalEntity entity;
    private ProfessionalDTO dto;

    @BeforeEach
    void setUp() {
        createDTO = new ProfessionalCreatedDTO();
        createDTO.setNome("Ana Lopes");
        createDTO.setSpecialty("Nail Designer");
        createDTO.setActive(true);

        entity = new ProfessionalEntity();
        entity.setId(1L);
        entity.setName("Ana Lopes");
        entity.setSpecialty("Nail Designer");
        entity.setActive(true);

        dto = new ProfessionalDTO();
        dto.setId(1L);
        dto.setName("Ana Lopes");
        dto.setSpecialty("Nail Designer");
        dto.setActive(true);
    }

    @Test
    void createProfessional_Success() {
        when(professionalMapper.toEntity(createDTO)).thenReturn(entity);
        when(professionalRepository.save(entity)).thenReturn(entity);
        when(professionalMapper.toDTO(entity)).thenReturn(dto);

        ProfessionalDTO result = professionalService.createProfessional(createDTO);

        assertNotNull(result);
        assertEquals("Ana Lopes", result.getName());

        verify(professionalMapper).toEntity(createDTO);
        verify(professionalRepository).save(entity);
        verify(professionalMapper).toDTO(entity);
    }

    @Test
    void findById_Success() {
        when(professionalRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(professionalMapper.toDTO(entity)).thenReturn(dto);

        ProfessionalDTO result = professionalService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(professionalRepository).findById(1L);
    }

    @Test
    void findById_ThrowsResourceNotFoundException() {
        when(professionalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> professionalService.findById(99L));

        verify(professionalRepository).findById(99L);
    }

    @Test
    void findAllActive_Success() {
        when(professionalRepository.findByActiveTrue()).thenReturn(List.of(entity));
        when(professionalMapper.toDTO(entity)).thenReturn(dto);

        List<ProfessionalDTO> results = professionalService.findAllActive();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Ana Lopes", results.get(0).getName());

        verify(professionalRepository).findByActiveTrue();
    }
}
