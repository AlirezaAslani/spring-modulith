package com.farabitech.smartparking_system.entry.spi.event;

import java.time.LocalDateTime;

public record VehicleEnteredEvent(String vehicleNumber,
                                  LocalDateTime entryTime) {
}
