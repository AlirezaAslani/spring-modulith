package com.farabitech.smartparking_system.billing.internal;

import com.farabitech.smartparking_system.billing.internal.repository.BillingRecordRepository;

import com.farabitech.smartparking_system.billing.spi.BillingSPI;
import com.farabitech.smartparking_system.billing.spi.dto.BillingDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
//TODO add edot span annotation
@Service
public class BillingManagement implements BillingSPI {


    private final BillingRecordRepository billingRecordRepository;

    public BillingManagement(BillingRecordRepository billingRecordRepository) {
        this.billingRecordRepository = billingRecordRepository;
    }

    @Override
    public Collection<BillingDTO> getInvoices() {
        ArrayList<BillingDTO> billingDTOs = new ArrayList<>();
         billingRecordRepository.findAll().forEach(billingRecord -> {
            billingDTOs.add(new BillingDTO(billingRecord.getAmount()));
        });
        return  billingDTOs;
    }
}
