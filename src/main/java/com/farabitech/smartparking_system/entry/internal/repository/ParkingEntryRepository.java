package com.farabitech.smartparking_system.entry.internal.repository;

import com.farabitech.smartparking_system.entry.internal.model.ParkingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingEntryRepository extends JpaRepository<ParkingEntry,Long> {
    Optional<ParkingEntry> findByVehicleNumberAndActiveTrue(String vehicleNumber);
}
