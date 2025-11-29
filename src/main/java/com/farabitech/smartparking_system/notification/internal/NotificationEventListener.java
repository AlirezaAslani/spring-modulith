package com.farabitech.smartparking_system.notification.internal;

import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.api.trace.SpanKind;
import org.springframework.stereotype.Service;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.modulith.events.ApplicationModuleListener;
import com.farabitech.smartparking_system.entry.spi.event.VehicleEnteredEvent;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;

@Slf4j
@Service
public class NotificationEventListener {

    @ApplicationModuleListener
    @WithSpan(value = "NotificationEventListener#notifyOnVehicleEntry", kind = SpanKind.CONSUMER)
    public void notifyOnVehicleEntry(VehicleEnteredEvent event) {

        log.info("Notification event received: vehicle={} entryTime={}",
                event.vehicleNumber(),
                event.entryTime());

        // Logic to send notification to the user
        System.out.println("📩 Notification: Vehicle " + event.vehicleNumber() +
                " entered at " + event.entryTime() + ". Welcome!");
    }

    @ApplicationModuleListener
    @WithSpan(value = "NotificationEventListener#notifyOnVehicleExit", kind = SpanKind.CONSUMER)
    public void notifyOnVehicleExit(VehicleExitedEvent event) {
        log.info("Notification event received: vehicle={} exitTime={}",
                event.vehicleNumber(),
                event.exitTime());

        // Logic to send notification to the user
        System.out.println("📩 Notification: Vehicle " + event.vehicleNumber() + " has exited. Thank you for visiting!");
    }
}
