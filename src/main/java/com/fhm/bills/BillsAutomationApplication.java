package com.fhm.bills;

import com.fhm.bills.selenium.WebAutomation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillsAutomationApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(BillsAutomationApplication.class, args);
		WebAutomation.runTask();
	}

}
