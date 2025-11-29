package com.farabitech.smartparking_system.billing.internal;

import java.util.ArrayList;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.api.trace.SpanKind;
import org.springframework.stereotype.Service;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import com.farabitech.smartparking_system.billing.spi.BillingSPI;
import com.farabitech.smartparking_system.billing.spi.dto.BillingDTO;
import com.farabitech.smartparking_system.billing.internal.repository.BillingRecordRepository;

@Slf4j
@Service
public class BillingManagement implements BillingSPI {


    private final BillingRecordRepository billingRecordRepository;

    public BillingManagement(BillingRecordRepository billingRecordRepository) {
        this.billingRecordRepository = billingRecordRepository;
    }

    @Override
    @WithSpan(value = "BillingManagement#getInvoices", kind = SpanKind.INTERNAL)
    public Collection<BillingDTO> getInvoices() {
        ArrayList<BillingDTO> billingDTOs = new ArrayList<>();
         billingRecordRepository.findAll().forEach(billingRecord -> {
            billingDTOs.add(new BillingDTO(billingRecord.getAmount()));
        });
        return  billingDTOs;
    }
}
