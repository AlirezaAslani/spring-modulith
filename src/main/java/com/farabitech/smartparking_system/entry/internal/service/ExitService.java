package com.farabitech.smartparking_system.entry.internal.service;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import com.farabitech.smartparking_system.entry.internal.model.ParkingEntry;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;
import com.farabitech.smartparking_system.entry.spi.exceptions.EntryNotFoundException;
import com.farabitech.smartparking_system.entry.internal.repository.ParkingEntryRepository;

@Slf4j
@Service
public class ExitService {

    private final ParkingEntryRepository repository;
    private final ApplicationEventPublisher publisher;

    public ExitService(ParkingEntryRepository repository,
                       ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }


    @Transactional
    public void vehicleExit(String vehicleNumber) {
        log.info("Processing vehicle exit: vehicleNumber={}", vehicleNumber);

        ParkingEntry entry = repository.findByVehicleNumberAndActiveTrue(vehicleNumber)
                .orElseThrow(() -> {
                    log.warn("Active entry not found for vehicleNumber={}", vehicleNumber);
                    return EntryNotFoundException.forVehicleEntry(vehicleNumber);
                });

        entry.setExitTime(LocalDateTime.now());
        entry.setActive(false);

        ParkingEntry updatedEntry = repository.save(entry);

        log.debug("Updated parking entry: id={} vehicleNumber={} entryTime={} exitTime={}",
                updatedEntry.getId(),
                updatedEntry.getVehicleNumber(),
                updatedEntry.getEntryTime(),
                updatedEntry.getExitTime());


        publisher.publishEvent(new VehicleExitedEvent(vehicleNumber, entry.getEntryTime(), entry.getExitTime()));

        log.info("Vehicle exit event published: vehicleNumber={} exitTime={}",
                vehicleNumber,
                updatedEntry.getExitTime());
    }
}
