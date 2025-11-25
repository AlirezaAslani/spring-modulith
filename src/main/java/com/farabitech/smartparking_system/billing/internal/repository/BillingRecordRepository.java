package com.farabitech.smartparking_system.billing.internal.repository;

import com.farabitech.smartparking_system.billing.internal.model.BillingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRecordRepository extends JpaRepository<BillingRecord,Long> {
}
