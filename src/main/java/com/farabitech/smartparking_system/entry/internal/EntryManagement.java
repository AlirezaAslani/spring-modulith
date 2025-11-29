package com.farabitech.smartparking_system.entry.internal;

import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.api.trace.SpanKind;
import org.springframework.stereotype.Service;
import com.farabitech.smartparking_system.entry.spi.EntrySPI;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.context.ApplicationEventPublisher;
import com.farabitech.smartparking_system.entry.internal.service.EntryService;
import com.farabitech.smartparking_system.entry.internal.service.ExitService;

@Slf4j
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
        log.info("Processing vehicle entry in EntryManagement: vehicleNumber={}", vehicleNumber);

        entryService.vehicleEntry(vehicleNumber);

        log.info("Vehicle entry completed in EntryManagement: vehicleNumber={}", vehicleNumber);

    }

    @Override
    @WithSpan(value = "EntryManagement#vehicleExit", kind = SpanKind.INTERNAL)
    public void vehicleExit(String vehicleNumber) {
        log.info("Processing vehicle exit in EntryManagement: vehicleNumber={}", vehicleNumber);

        exitService.vehicleExit(vehicleNumber);

        log.info("Vehicle exit completed in EntryManagement: vehicleNumber={}", vehicleNumber);

    }
}
