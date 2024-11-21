package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.Client;
import com.park.demo_park_api.exception.CpfUniqueViolationException;
import com.park.demo_park_api.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
}
