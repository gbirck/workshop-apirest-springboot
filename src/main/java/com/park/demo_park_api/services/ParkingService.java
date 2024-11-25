package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.Client;
import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.entities.Spot;
import com.park.demo_park_api.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional
    public ClientSpot checkOut(String receipt) {
        ClientSpot clientSpot = clientSpotService.findByReceipt(receipt);

        LocalDateTime exitDate = LocalDateTime.now();

        BigDecimal value = ParkingUtils.calculateCost(clientSpot.getEntryDate(), exitDate);
        clientSpot.setValue(value);

        long totalTimes = clientSpotService.getTotalTimesParkingCompleteByClientCpf(clientSpot.getClient().getCpf());

        BigDecimal discount = ParkingUtils.calculateDiscount(value, totalTimes);
        clientSpot.setDiscount(discount);

        clientSpot.setExitDate(exitDate);
        clientSpot.getSpot().setStatus(Spot.SpotStatus.FREE);

        return clientSpotService.insert(clientSpot);
    }
}
