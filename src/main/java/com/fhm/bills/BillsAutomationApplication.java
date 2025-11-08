package com.fhm.bills;

import com.fhm.bills.selenium.WebAutomation;
import com.fhm.bills.util.AesUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BillsAutomationApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(BillsAutomationApplication.class, args);
		WebAutomation webAutomation = context.getBean(WebAutomation.class);
		webAutomation.runTask();
	}

}
