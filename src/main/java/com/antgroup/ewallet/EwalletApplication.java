package com.antgroup.ewallet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import sdk.code.model.result.CodeIdentificationInitResult;
import sdk.code.service.CodeIdentificationService;

@SpringBootApplication
@PropertySource("classpath:ac-sdk.properties")
@ImportResource({"classpath:/META-INF/config/component-server-sdk.xml"})
public class EwalletApplication {
	private static final Logger logger = LogManager.getLogger(com.antgroup.ewallet.EwalletApplication.class);

	private static CodeIdentificationService codeIdentificationService;

	@Autowired
	public EwalletApplication(CodeIdentificationService codeIdentificationService) {
		com.antgroup.ewallet.EwalletApplication.codeIdentificationService = codeIdentificationService;
	}

	public static void main(String[] args) {
		SpringApplication.run(com.antgroup.ewallet.EwalletApplication.class, args);
		logger.info("Start the A+ code identification ...");

		// Initialized the A+ Server SDK.
		CodeIdentificationInitResult result = codeIdentificationService.init();
		if (result.getResult().getResultStatus().equals("S")){
			// Initialization success.
			logger.info("A+ Server SDK init successful.");
		} else {
			// Block the application startup and troubleshoot
			logger.error("A+ Server SDK init failed.");
			System.exit(0);
		}
	}

}
