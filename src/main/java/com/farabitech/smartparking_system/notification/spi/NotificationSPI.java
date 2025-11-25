package com.farabitech.smartparking_system.notification.spi;

import com.farabitech.smartparking_system.billing.spi.dto.BillingDTO;

import java.util.Collection;


public interface NotificationSPI {
    Collection<BillingDTO> getInvoices();
}
