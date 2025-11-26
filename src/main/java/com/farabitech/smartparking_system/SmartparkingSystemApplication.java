package com.farabitech.smartparking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@Modulithic(sharedModules = {"common"})
public class SmartparkingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartparkingSystemApplication.class, args);
	}


}
