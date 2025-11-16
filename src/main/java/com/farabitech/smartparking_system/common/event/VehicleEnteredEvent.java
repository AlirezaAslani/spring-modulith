package com.farabitech.smartparking_system.common.event;

import java.time.LocalDateTime;

public record VehicleEnteredEvent(String vehicleNumber,
                                  LocalDateTime entryTime) {
}
