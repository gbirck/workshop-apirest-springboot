package com.park.demo_park_api.repositories;

import com.park.demo_park_api.entities.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    Optional<Spot> findByCode(String code);
}
