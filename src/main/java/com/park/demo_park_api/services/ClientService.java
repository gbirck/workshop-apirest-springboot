package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.Client;
import com.park.demo_park_api.exception.CpfUniqueViolationException;
import com.park.demo_park_api.exception.EntityNotFoundException;
import com.park.demo_park_api.repositories.ClientRepository;
import com.park.demo_park_api.repositories.projection.ClientProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client insert(Client client) {
        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new CpfUniqueViolationException(
                    String.format("CPF %s already exists", client.getCpf())
            );
        }
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Client id=%s not found", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<ClientProjection> findAll(Pageable pageable) {
        return clientRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Client findByUserId(Long id) {
        return clientRepository.findByUserId(id);
    }

    @Transactional(readOnly = true)
    public Client findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Client with cpf %s not found", cpf))
        );
    }
}
