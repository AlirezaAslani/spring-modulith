package com.farabitech.smartparking_system.entry.internal;

import com.farabitech.smartparking_system.entry.internal.repository.ParkingEntryRepository;
import com.farabitech.smartparking_system.entry.internal.service.EntryService;
import com.farabitech.smartparking_system.entry.internal.service.ExitService;
import com.farabitech.smartparking_system.entry.spi.EntrySPI;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EntryManagement implements EntrySPI {

    private final EntryService entryService;
    private final ExitService exitService;
    private final ApplicationEventPublisher publisher;

    public EntryManagement(EntryService entryService, ExitService exitService, ApplicationEventPublisher publisher) {
        this.entryService = entryService;
        this.exitService = exitService;
        this.publisher = publisher;
    }

    @Override
    public void vehicleEntry(String vehicleNumber) {
        entryService.vehicleEntry(vehicleNumber);
    }

    @Override
    public void vehicleExit(String vehicleNumber) {
        exitService.vehicleExit(vehicleNumber);
    }
}
