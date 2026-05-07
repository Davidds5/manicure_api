package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.exceptions.GlobalExceptionHandler;
import br.com.davidds5.manicure_api.service.ProfessionalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.http.MediaType;


@ExtendWith(MockitoExtension.class)
class ProfessionalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfessionalService professionalService;
    
    @InjectMocks
    private ProfessionalController professionalController;

    private ObjectMapper objectMapper = new ObjectMapper();
    

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(professionalController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    }

    @Test
    void createProfessional_ValidDto_ReturnsCreated() throws Exception{

        ProfessionalCreatedDTO requestDto = new ProfessionalCreatedDTO();
        requestDto.setName("Antonio Cicero");
        requestDto.setSpecialty("Manicure");
        requestDto.setEmail("antonio@email.com");
        requestDto.setPassword("123456");
        requestDto.setActive(true);

        ProfessionalDTO responseDto = new ProfessionalDTO();
        responseDto.setName("Antonio Cicero");
        responseDto.setSpecialty("Manicure");
        responseDto.setActive(true);

        Mockito.when(professionalService.createProfessional(any(ProfessionalCreatedDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/professionals")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Antonio Cicero"));
    }
    
}
    
