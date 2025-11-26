package com.farabitech.smartparking_system.allocation.internal.config;

import com.farabitech.smartparking_system.allocation.internal.model.Slot;
import com.farabitech.smartparking_system.allocation.internal.repository.SlotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class SlotInitializer {

    private final SlotRepository repo;

    @PostConstruct
    void init() {
        if (repo.count() == 0) {
            repo.save(new Slot(null, "A1", true, null));
            repo.save(new Slot(null, "A2", true, null));
            repo.save(new Slot(null, "A3", true, null));
        }
    }
}
