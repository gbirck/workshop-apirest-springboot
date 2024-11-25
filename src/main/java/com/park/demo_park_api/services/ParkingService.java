package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.Client;
import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.entities.Spot;
import com.park.demo_park_api.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ClientSpotService clientSpotService;
    private final ClientService clientService;
    private final SpotService spotService;

    @Transactional
    public ClientSpot checkIn(ClientSpot clientSpot) {
        Client client = clientService.findByCpf(clientSpot.getClient().getCpf());
        clientSpot.setClient(client);

        Spot spot = spotService.findForFreeSpot();
        spot.setStatus(Spot.SpotStatus.OCCUPIED);
        clientSpot.setSpot(spot);

        clientSpot.setEntryDate(LocalDateTime.now());
        clientSpot.setReceipt(ParkingUtils.generateReceipt());

        return clientSpotService.insert(clientSpot);
    }
}
