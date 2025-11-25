package com.farabitech.smartparking_system.entry.spi.event;

import java.time.LocalDateTime;

public record VehicleExitedEvent(String vehicleNumber,
                                 LocalDateTime entryTime,LocalDateTime exitTime) {
}
