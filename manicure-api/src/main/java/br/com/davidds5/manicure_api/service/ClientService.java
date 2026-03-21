package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.dto.ClientUpdateDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ClientMapper;
import br.com.davidds5.manicure_api.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientDTO createClient(ClientCreatedDTO dto) {
        log.info("Criando novo cliente: {}", dto.getEmail());

        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        ClientEntity entity = clientMapper.toEntity(dto);
        ClientEntity saved = clientRepository.save(entity);

        log.info("Cliente criado com ID: {}", saved.getId());
        return clientMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        log.info("Buscando cliente por ID: {}", id);

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        return clientMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(Pageable pageable) {
        log.info("Listando clientes");

        return clientRepository.findAll(pageable)
                .map(clientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ClientDTO> findByNameContaining(String name, Pageable pageable) {
        log.info("Buscando clientes por nome: {}", name);

        return clientRepository.findByNameContaining(name, pageable)
                .map(clientMapper::toDTO);
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientUpdateDTO dto) {
        log.info("Atualizando cliente ID: {}", id);

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        // Atualiza email se vier e for diferente
        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new BusinessException("Email já cadastrado: " + dto.getEmail());
            }
            existing.setEmail(dto.getEmail());
        }

        // Atualizações parciais (evita sobrescrever com null)
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        if (dto.getPhone() != null) {
            existing.setPhone(dto.getPhone());
        }

        ClientEntity updated = clientRepository.save(existing);

        log.info("Cliente atualizado com ID: {}", updated.getId());
        return clientMapper.toDTO(updated);
    }

    @Transactional
    public void deleteClient(Long id) {
        log.info("Deletando cliente ID: {}", id);

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        clientRepository.delete(existing);
        log.info("Cliente deletado com ID: {}", id);
    }
}