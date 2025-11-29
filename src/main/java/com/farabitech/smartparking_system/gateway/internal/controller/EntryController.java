package com.farabitech.smartparking_system.gateway.internal.controller;

import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.api.trace.SpanKind;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import com.farabitech.smartparking_system.entry.spi.EntrySPI;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@RequestMapping("/parking")
public class EntryController {

private final EntrySPI entrySPI;

    public EntryController(EntrySPI entrySPI) {
        this.entrySPI = entrySPI;
    }

    @PostMapping("/entry")
    @WithSpan(value = "EntryController#entry", kind = SpanKind.SERVER)
    public ResponseEntity<String> entry(@RequestParam String vehicleNumber) {
        log.info("Received request for vehicle entry: vehicleNumber={}", vehicleNumber);

        entrySPI.vehicleEntry(vehicleNumber);

        log.info("Vehicle entry processed successfully: vehicleNumber={}", vehicleNumber);

        return ResponseEntity.ok("Vehicle entered: " + vehicleNumber);
    }

    @PostMapping("/exit")
    @WithSpan(value = "EntryController#exit", kind = SpanKind.SERVER)
    public ResponseEntity<String> exit(@RequestParam String vehicleNumber) {

        log.info("Received request for vehicle exit: vehicleNumber={}", vehicleNumber);

        entrySPI.vehicleExit(vehicleNumber);

        log.info("Vehicle exit processed successfully: vehicleNumber={}", vehicleNumber);

        return ResponseEntity.ok("Vehicle exited: " + vehicleNumber);
    }
}
