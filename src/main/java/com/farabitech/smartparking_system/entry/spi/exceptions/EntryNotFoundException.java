package com.farabitech.smartparking_system.entry.spi.exceptions;

public class EntryNotFoundException extends RuntimeException {
    public EntryNotFoundException(String message) {
        super(message);
    }

    public static EntryNotFoundException forVehicleEntry(String vehicleNumber) {
        return new EntryNotFoundException("Entry with vehicleNumber " + vehicleNumber + " not found");
    }
}
