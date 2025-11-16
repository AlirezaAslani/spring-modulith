package com.farabitech.smartparking_system.common.event;

import java.time.LocalDateTime;

public record VehicleExitedEvent(String vehicleNumber,
                                 LocalDateTime entryTime,LocalDateTime exitTime) {
}
