package com.farabitech.smartparking_system.billing.spi;

import com.farabitech.smartparking_system.billing.spi.dto.BillingDTO;

import java.util.Collection;


public interface BillingSPI {
    Collection<BillingDTO> getInvoices();
}
