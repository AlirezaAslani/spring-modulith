package com.farabitech.smartparking_system.allocation.internal;

import com.farabitech.smartparking_system.allocation.internal.service.SlotAllocationService;
import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class AllocationEventListener {

    private final SlotAllocationService  slotAllocationService;

    public AllocationEventListener(SlotAllocationService slotAllocationService) {
        this.slotAllocationService = slotAllocationService;
    }

    @ApplicationModuleListener//doesnt apply TransactionalEventListener
    public void handleVehicleEntry(VehicleEnteredEvent event) {

        slotAllocationService.handleVehicleEntry(event);
    }

    @ApplicationModuleListener
    public void handleVehicleExit(VehicleExitedEvent event) {
        slotAllocationService.handleVehicleExit(event);

    }

}
