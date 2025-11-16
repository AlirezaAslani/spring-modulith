package com.farabitech.smartparking_system.allocation.repository;

import com.farabitech.smartparking_system.allocation.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot,Long> {

    Optional<Slot> findFirstByAvailableTrue();

    Optional<Slot> findByVehicleNumber(String vehicleNumber);
}
