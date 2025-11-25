package com.farabitech.smartparking_system.allocation.service;

import com.farabitech.smartparking_system.allocation.model.Slot;
import com.farabitech.smartparking_system.allocation.repository.SlotRepository;
import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;

import org.springframework.stereotype.Service;

@Service
public class SlotAllocationService {

    private final SlotRepository slotRepository;

    public SlotAllocationService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }


    //@Async
    //@Transactional(propagation = Propagation.REQUIRES_NEW)

    @ApplicationModuleListener //doesnt apply TransactionalEventListener
    public void handleVehicleEntry(VehicleEnteredEvent event) {

        Slot slot = slotRepository.findFirstByAvailableTrue()
                .orElseThrow(() -> new RuntimeException("🚫 No available slots!"));
        slot.setAvailable(false);
        slot.setVehicleNumber(event.vehicleNumber());
        slotRepository.save(slot);

        System.out.println("🅿️ Allocated Slot " + slot.getSlotCode() + " to vehicle " + event.vehicleNumber());
    }

   @ApplicationModuleListener
    public void handleVehicleExit(VehicleExitedEvent event) {
        slotRepository.findByVehicleNumber(event.vehicleNumber())
                .ifPresentOrElse(slot -> {
                    slot.setAvailable(true); // free the slot
                    slot.setVehicleNumber(null);
                    slotRepository.save(slot);
                    System.out.println("🅿️ Freed Slot " + slot.getSlotCode() + " from vehicle " + event.vehicleNumber());
                }, () -> {
                    throw new RuntimeException("🚫 No slot found for vehicle " + event.vehicleNumber());
                });
    }


}
