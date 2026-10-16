package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.dto.ClientUpdateDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.TenantEntity;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.DuplicateResourceException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ClientMapper;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.TenantRepository;
import br.com.davidds5.manicure_api.specification.ClientSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    @Transactional
    public ClientDTO createClient(ClientCreatedDTO dto) {
        log.info("Criando novo cliente: {}", dto.getEmail());

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null) {
            throw new BusinessException("Contexto de salão (tenant) obrigatório para cadastrar cliente.");
        }

        if (clientRepository.findByEmailAndTenantId(dto.getEmail(), currentTenantId).isPresent()) {
            throw new DuplicateResourceException("E-mail já cadastrado neste salão: " + dto.getEmail());
        }

        ClientEntity entity = clientMapper.toEntity(dto);
        entity.setTenantId(currentTenantId);
        String senhaCriptografada = passwordEncoder.encode(dto.getPassword());
        entity.setPassword(senhaCriptografada);
        ClientEntity saved = clientRepository.save(entity);

        log.info("Cliente criado com ID: {}", saved.getId());
        return clientMapper.toDTO(saved);
    }

    @Transactional
    public ClientDTO createPublicClient(String tenantSlug, ClientCreatedDTO dto) {
        log.info("Criando novo cliente público no salão {}: {}", tenantSlug, dto.getEmail());

        TenantEntity tenant = tenantRepository.findBySlugAndDeletedAtIsNull(tenantSlug.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Salão não encontrado com o slug: " + tenantSlug));

        if (tenant.getStatus() == TenantStatus.SUSPENDED) {
            throw new ResourceNotFoundException("Salão não encontrado ou suspenso: " + tenantSlug);
        }

        Long tenantId = tenant.getId();
        if (clientRepository.findByEmailAndTenantId(dto.getEmail(), tenantId).isPresent()) {
            throw new DuplicateResourceException("E-mail já cadastrado neste salão: " + dto.getEmail());
        }

        ClientEntity entity = clientMapper.toEntity(dto);
        entity.setTenantId(tenantId);
        String senhaCriptografada = passwordEncoder.encode(dto.getPassword());
        entity.setPassword(senhaCriptografada);
        ClientEntity saved = clientRepository.save(entity);

        log.info("Cliente público criado com ID: {} no tenant: {}", saved.getId(), tenantId);
        return clientMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        log.info("Buscando cliente por ID: {}", id);

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(entity.getTenantId())) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }

        return clientMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(String name, String email, String phone, Pageable pageable) {
        log.info("Listando clientes com filtros dinamicos");

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null) {
            throw new BusinessException("Contexto de salão (tenant) obrigatório.");
        }

        Specification<ClientEntity> spec = (root, query, cb) -> cb.equal(root.get("tenantId"), currentTenantId);

        if (name != null && !name.isBlank()) {
            spec = spec.and(ClientSpecification.nameContains(name));
        }

        if (email != null && !email.isBlank()) {
            spec = spec.and(ClientSpecification.emailContains(email));
        }

        if (phone != null && !phone.isBlank()) {
            spec = spec.and(ClientSpecification.phoneContains(phone));
        }

        return clientRepository.findAll(spec, pageable).map(clientMapper::toDTO);
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientUpdateDTO dto) {
        log.info("Atualizando cliente ID: {}", id);

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(existing.getTenantId())) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            if (clientRepository.findByEmailAndTenantId(dto.getEmail(), currentTenantId).isPresent()) {
                throw new DuplicateResourceException("E-mail já cadastrado neste salão: " + dto.getEmail());
            }
            existing.setEmail(dto.getEmail());
        }

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

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(existing.getTenantId())) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }

        clientRepository.delete(existing);
        log.info("Cliente deletado com ID: {}", id);
    }
}