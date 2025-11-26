package com.farabitech.smartparking_system.gateway.internal.controller;

import com.farabitech.smartparking_system.billing.spi.BillingSPI;
import com.farabitech.smartparking_system.billing.spi.dto.BillingDTO;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporting")
public class ReportController {

    private final BillingSPI billingSPI;

    public ReportController(BillingSPI billingSPI) {
        this.billingSPI = billingSPI;
    }

    @GetMapping({"/invoices"})
    @WithSpan(value = "ReportController#getInvoicesSummery", kind = SpanKind.SERVER)
    Double getInvoicesSummery(Model model, HttpSession session) {
        return billingSPI.getInvoices().stream()
                .mapToDouble(BillingDTO::amount)
                .sum();
    }
}
