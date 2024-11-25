package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.exception.EntityNotFoundException;
import com.park.demo_park_api.repositories.ClientSpotRepository;
import com.park.demo_park_api.repositories.projection.ClientSpotProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientSpotService {

    private final ClientSpotRepository clientSpotRepository;

    @Transactional
    public ClientSpot insert(ClientSpot clientSpot) {
        return clientSpotRepository.save(clientSpot);
    }

    @Transactional(readOnly = true)
    public ClientSpot findByReceipt(String receipt) {
        return clientSpotRepository.findByReceipt(receipt).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Receipt '%s' not found in the system or checkout already performed", receipt)
                )
        );
    }

    @Transactional(readOnly = true)
    public long getTotalTimesParkingCompleteByClientCpf(String cpf) {
        return clientSpotRepository.countByClientCpfAndExitDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpotProjection> findAllByClientCpf(String cpf, Pageable pageable) {
        return clientSpotRepository.findAllByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpotProjection> findAllClientParkingByUserId(Long id, Pageable pageable) {
        return clientSpotRepository.findAllByClientUserId(id, pageable);
    }
}
