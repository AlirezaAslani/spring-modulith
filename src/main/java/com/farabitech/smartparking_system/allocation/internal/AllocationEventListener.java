package com.farabitech.smartparking_system.allocation.internal;

import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.api.trace.SpanKind;
import org.springframework.stereotype.Service;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.modulith.events.ApplicationModuleListener;
import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;
import com.farabitech.smartparking_system.allocation.internal.service.SlotAllocationService;

@Slf4j
@Service
public class AllocationEventListener {

    private final SlotAllocationService  slotAllocationService;

    public AllocationEventListener(SlotAllocationService slotAllocationService) {
        this.slotAllocationService = slotAllocationService;
    }

    @ApplicationModuleListener
    @WithSpan(value = "AllocationEventListener#handleVehicleEntry", kind = SpanKind.CONSUMER)
    public void handleVehicleEntry(VehicleEnteredEvent event) {

        log.info("Received VehicleEnteredEvent: vehicleNumber={} entryTime={}",
                event.vehicleNumber(), event.entryTime());

        slotAllocationService.handleVehicleEntry(event);

        log.info("Processed VehicleEnteredEvent for vehicleNumber={}", event.vehicleNumber());

    }

    @ApplicationModuleListener
    @WithSpan(value = "AllocationEventListener#handleVehicleExit", kind = SpanKind.CONSUMER)
    public void handleVehicleExit(VehicleExitedEvent event) {

        log.info("Received VehicleExitedEvent: vehicleNumber={} exitTime={}",
                event.vehicleNumber(), event.exitTime());

        slotAllocationService.handleVehicleExit(event);

        log.info("Processed VehicleExitedEvent for vehicleNumber={}", event.vehicleNumber());

    }

}
