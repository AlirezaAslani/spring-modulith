package com.farabitech.smartparking_system.billing.internal;

import com.farabitech.smartparking_system.billing.internal.model.BillingRecord;
import com.farabitech.smartparking_system.billing.internal.repository.BillingRecordRepository;
import com.farabitech.smartparking_system.entry.spi.event.VehicleExitedEvent;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class BillingEventListener {


    //TODO implement calculation in service
    private final BillingRecordRepository billingRecordRepository;

    public BillingEventListener(BillingRecordRepository billingRecordRepository) {
        this.billingRecordRepository = billingRecordRepository;
    }

    @ApplicationModuleListener
    @WithSpan(value = "BillingEventListener#handleVehicleExit", kind = SpanKind.CONSUMER)
    public void handleVehicleExit(VehicleExitedEvent event) {

        Duration duration = Duration.between(event.entryTime(), event.exitTime());
        double amount = Math.max(20, (duration.toMinutes() / 60.0) * 50); //$50/hour

        BillingRecord record = new BillingRecord(null, event.vehicleNumber(), amount, event.exitTime());
        billingRecordRepository.save(record);

        System.out.println("✅ Billed $" + amount + " for vehicle " + event.vehicleNumber() +
                " from " + event.entryTime() + " to " + event.exitTime());
    }
}
