package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.exceptions.GlobalExceptionHandler;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createClient_ValidDto_ReturnsCreated() throws Exception {
        // Arrange
        ClientCreatedDTO requestDto = new ClientCreatedDTO();
        requestDto.setName("João Testador");
        requestDto.setEmail("joao@email.com");
        requestDto.setPhone("11999998888");

        ClientDTO responseDto = new ClientDTO();
        responseDto.setId(10L);
        responseDto.setName("João Testador");
        responseDto.setEmail("joao@email.com");
        responseDto.setPhone("11999998888");

        Mockito.when(clientService.createClient(any(ClientCreatedDTO.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("João Testador"));
    }

    @Test
    void createClient_InvalidDto_ReturnsBadRequest() throws Exception {

        ClientCreatedDTO requestDto = new ClientCreatedDTO();
        requestDto.setName("");
        requestDto.setEmail("email-invalido");
        requestDto.setPhone("11988888888");

        mockMvc.perform(post("/api/v1/clients")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(requestDto)))
    .andExpect(status().isBadRequest())
    .andExpect(jsonPath("$.error").value("Validacao falhou"))
    .andExpect(jsonPath("$.message").value("Um ou mais campos estao invalidos"))
    .andExpect(jsonPath("$.errors").isArray());        
    } 

    @Test
    void findById_ValidId_ReturnsOK() throws Exception {

        ClientDTO responseDto = new ClientDTO();
        responseDto.setId(1L);
        responseDto.setName("Maria da Silva");

        Mockito.when(clientService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/clients/1")
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(1L))
    .andExpect(jsonPath("$.name").value("Maria da Silva"));
    }

    @Test
    void findById_InvalidId_ReturnsNotFound() throws Exception {
        Mockito.when(clientService.findById(999L))
        .thenThrow(new ResourceNotFoundException("Cliente nao encontrado com o ID: 999"));

        mockMvc.perform(get("/api/v1/clients/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Recurso nao encontrado"));
     
    }

    @Test
    void delete_ValidId_ReturnsNoContent() throws Exception{
        // Arrange - Ensinando o duble a fazer nada quando mandamos deletar o ID 1
        Mockito.doNothing().when(clientService).deleteClient(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/clients/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
    }
}
