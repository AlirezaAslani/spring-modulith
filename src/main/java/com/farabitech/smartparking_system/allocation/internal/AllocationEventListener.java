package com.farabitech.smartparking_system.allocation.internal;

import com.farabitech.smartparking_system.allocation.internal.service.SlotAllocationService;
import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class AllocationEventListener {

    private final SlotAllocationService  slotAllocationService;

    public AllocationEventListener(SlotAllocationService slotAllocationService) {
        this.slotAllocationService = slotAllocationService;
    }

    @ApplicationModuleListener
    @WithSpan(value = "AllocationEventListener#handleVehicleEntry", kind = SpanKind.CONSUMER)
    public void handleVehicleEntry(VehicleEnteredEvent event) {

        slotAllocationService.handleVehicleEntry(event);
    }

    @ApplicationModuleListener
    @WithSpan(value = "AllocationEventListener#handleVehicleExit", kind = SpanKind.CONSUMER)
    public void handleVehicleExit(VehicleExitedEvent event) {
        slotAllocationService.handleVehicleExit(event);

    }

}
