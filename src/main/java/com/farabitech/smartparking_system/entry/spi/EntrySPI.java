package com.farabitech.smartparking_system.entry.spi;

public interface EntrySPI {
    void vehicleEntry(String vehicleNumber);
    void vehicleExit(String vehicleNumber);
}
