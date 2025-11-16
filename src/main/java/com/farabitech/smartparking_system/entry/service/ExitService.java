package com.farabitech.smartparking_system.entry.service;

import com.farabitech.smartparking_system.entry.model.ParkingEntry;
import com.farabitech.smartparking_system.entry.repository.ParkingEntryRepository;
import com.farabitech.smartparking_system.common.event.VehicleExitedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExitService {

    // save vehicle entry details to DB
    // allocate a parking slot
    // send notification to the user

    private final ParkingEntryRepository repository;
    private final ApplicationEventPublisher publisher;

    public ExitService(ParkingEntryRepository repository,
                       ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }


    public void vehicleExit(String vehicleNumber) {
        // get vehicle entry details from DB
        // update exit time
        // save to db
        //publish vehicle exited event
        ParkingEntry entry = repository.findByVehicleNumberAndActiveTrue(vehicleNumber)
                .orElseThrow(() -> new RuntimeException("🚫 No active entry found for vehicle " + vehicleNumber));

        entry.setExitTime(LocalDateTime.now());
        entry.setActive(false);
        repository.save(entry);
        publisher.publishEvent(new VehicleExitedEvent(vehicleNumber, entry.getEntryTime(), entry.getExitTime()));
    }
}
