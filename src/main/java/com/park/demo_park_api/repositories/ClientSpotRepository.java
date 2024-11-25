package com.park.demo_park_api.repositories;

import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.repositories.projection.ClientSpotProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientSpotRepository extends JpaRepository<ClientSpot, Long> {
    Optional<ClientSpot> findByReceipt(String receipt);

    long countByClientCpfAndExitDateIsNotNull(String cpf);

    Page<ClientSpotProjection> findAllByClientCpf(String cpf, Pageable pageable);

    Page<ClientSpotProjection> findAllByClientUserId(Long id, Pageable pageable);
}
