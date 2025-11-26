package com.farabitech.smartparking_system.gateway.internal.controller;

import com.farabitech.smartparking_system.entry.internal.service.EntryService;
import com.farabitech.smartparking_system.entry.internal.service.ExitService;
import com.farabitech.smartparking_system.entry.spi.EntrySPI;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        entrySPI.vehicleEntry(vehicleNumber);
        return ResponseEntity.ok("Vehicle entered: " + vehicleNumber);
    }

    @PostMapping("/exit")
    @WithSpan(value = "EntryController#exit", kind = SpanKind.SERVER)
    public ResponseEntity<String> exit(@RequestParam String vehicleNumber) {
        entrySPI.vehicleExit(vehicleNumber);
        return ResponseEntity.ok("Vehicle exited: " + vehicleNumber);
    }
}
