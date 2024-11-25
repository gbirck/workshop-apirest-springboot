package com.park.demo_park_api.repositories;

import com.park.demo_park_api.entities.ClientSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientSpotRepository extends JpaRepository<ClientSpot, Long> {
}
