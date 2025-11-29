package com.farabitech.smartparking_system.gateway.internal.controller;

import com.farabitech.smartparking_system.billing.spi.BillingSPI;
import com.farabitech.smartparking_system.billing.spi.dto.BillingDTO;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/reporting")
public class ReportController {

    private final BillingSPI billingSPI;

    public ReportController(BillingSPI billingSPI) {
        this.billingSPI = billingSPI;
    }

    @GetMapping({"/invoices"})
    @Timed(value = "latencyInSec.invoicesSummery")
    @Counted(value ="counter.invoicesSummery" )
    @WithSpan(value = "ReportController#getInvoicesSummery", kind = SpanKind.SERVER)
    Double getInvoicesSummery(Model model, HttpSession session) {
        log.info("Received request to get invoice summary");

        var invoices = billingSPI.getInvoices();


        log.debug("Fetched {} invoices from BillingSPI", invoices.size());

        double totalAmount = invoices.stream()
                .mapToDouble(BillingDTO::amount)
                .sum();

        log.info("Calculated invoice summary: totalAmount={}", totalAmount);

        return totalAmount;
    }
}
