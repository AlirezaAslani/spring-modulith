package com.farabitech.smartparking_system.entry.internal;

import com.farabitech.smartparking_system.entry.internal.repository.ParkingEntryRepository;
import com.farabitech.smartparking_system.entry.internal.service.EntryService;
import com.farabitech.smartparking_system.entry.internal.service.ExitService;
import com.farabitech.smartparking_system.entry.spi.EntrySPI;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
    @WithSpan(value = "EntryManagement#vehicleEntry", kind = SpanKind.INTERNAL)
    public void vehicleEntry(String vehicleNumber) {
        entryService.vehicleEntry(vehicleNumber);
    }

    @Override
    @WithSpan(value = "EntryManagement#vehicleExit", kind = SpanKind.INTERNAL)
    public void vehicleExit(String vehicleNumber) {
        exitService.vehicleExit(vehicleNumber);
    }
}
