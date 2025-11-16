package com.farabitech.smartparking_system.billing.repository;

import com.farabitech.smartparking_system.billing.model.BillingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRecordRepository extends JpaRepository<BillingRecord,Long> {
}
