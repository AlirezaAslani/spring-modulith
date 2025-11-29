package com.farabitech.smartparking_system.entry.internal.service;


import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import com.farabitech.smartparking_system.entry.internal.model.ParkingEntry;
import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.internal.repository.ParkingEntryRepository;

@Slf4j
@Service
public class EntryService {

    private final ParkingEntryRepository repository;
    private final ApplicationEventPublisher publisher;

    public EntryService(ParkingEntryRepository repository,
                        ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }


    @Transactional
    public void vehicleEntry(String vehicleNumber) {
        log.info("Processing vehicle entry: vehicleNumber={}", vehicleNumber);

        ParkingEntry parkingEntry = new ParkingEntry(null, vehicleNumber, LocalDateTime.now(), null, true);

        ParkingEntry savedEntry =  repository.save(parkingEntry);

        log.debug("Saved parking entry: id={} vehicleNumber={} entryTime={}",
                savedEntry.getId(),
                savedEntry.getVehicleNumber(),
                savedEntry.getEntryTime());

        publisher.publishEvent(new VehicleEnteredEvent(vehicleNumber, parkingEntry.getEntryTime()));


        log.info("Vehicle entry event published: vehicleNumber={} entryTime={}",
                vehicleNumber,
                savedEntry.getEntryTime());

    }
}
