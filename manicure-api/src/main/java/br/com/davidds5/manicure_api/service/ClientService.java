package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ClientMapper;
import br.com.davidds5.manicure_api.repository.ClientRepository;

import br.com.davidds5.manicure_api.util.Constants;
import br.com.davidds5.manicure_api.util.DateUtil;
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
    public ClientDTO createClient(@org.jetbrains.annotations.NotNull ClientCreatedDTO dto) {
        log.info("Criando novo cliente: {}", dto.getEmail());

        // Verifica se email já existe
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
        log.info("Listando todos os clientes com paginação");
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
    public ClientDTO updateClient(Long id, ClientCreatedDTO) {
        log.info("Atualizando cliente ID: {}", id);

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        // Verifica se email mudou e se já existe em outro cliente
        if (!existing.getEmail().equals(dto.getEmail())) {
            if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new BusinessException("Email já cadastrado: " + dto.getEmail());
            }
        }

        existing.setName(dto.getName());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());

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

    @Transactional
    public void cancelAppointment(Long id) {
        AppointmentEntity existing = getAppointment(id);

        if (!DateUtil.canCancel(existing.getDateTime())) {
            throw new BusinessException("Cancelamento só com " + Constants.CANCEL_HOURS_AHEAD + "h de antecedência");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CANCELLED);
    }
}