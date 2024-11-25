package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.Spot;
import com.park.demo_park_api.exception.CodeUniqueViolationException;
import com.park.demo_park_api.exception.EntityNotFoundException;
import com.park.demo_park_api.repositories.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.park.demo_park_api.entities.Spot.SpotStatus.FREE;

@RequiredArgsConstructor
@Service
public class SpotService {

    private final SpotRepository spotRepository;

    @Transactional
    public Spot insert(Spot spot) {
        try {
            return spotRepository.save(spot);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueViolationException(
                    String.format("Spot with code %s already registered", spot.getCode())
            );
        }
    }

    @Transactional(readOnly = true)
    public Spot findByCode(String code) {
        return spotRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Spot with code %s not found", code))
        );
    }

    @Transactional(readOnly = true)
    public Spot findForFreeSpot() {
        return spotRepository.findFirstByStatus(FREE).orElseThrow(
                () -> new EntityNotFoundException("No free spots found")
        );
    }
}
