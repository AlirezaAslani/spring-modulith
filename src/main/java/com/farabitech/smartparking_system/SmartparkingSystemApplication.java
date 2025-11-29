package com.farabitech.smartparking_system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.Modulithic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@Modulithic(sharedModules = {"common"})
public class SmartparkingSystemApplication {

	public static void main(String[] args) {
        log.info("Launching SmartParking System...");
		SpringApplication.run(SmartparkingSystemApplication.class, args);
        log.info("SmartParking System is now running!");
	}


}
