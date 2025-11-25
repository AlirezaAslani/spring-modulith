package com.farabitech.smartparking_system;

import com.farabitech.smartparking_system.allocation.model.Slot;
import com.farabitech.smartparking_system.allocation.repository.SlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith(sharedModules = {"common"})
public class SmartparkingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartparkingSystemApplication.class, args);
	}


}
