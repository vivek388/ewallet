package com.antgroup.ewallet

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import sdk.code.service.CodeIdentificationService

@SpringBootApplication
@PropertySource("classpath:ac-sdk.properties")
@ImportResource("classpath:/META-INF/config/component-server-sdk.xml")
class EwalletApplication @Autowired constructor(codeIdentificationService: CodeIdentificationService?) {
    init {
        Companion.codeIdentificationService = codeIdentificationService
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(
            EwalletApplication::class.java
        )

        private var codeIdentificationService: CodeIdentificationService? = null

        fun main(args: Array<String>) {
            SpringApplication.run(EwalletApplication::class.java, *args)
            logger.info("Start the A+ code identification ...")

            // Initialized the A+ Server SDK.
            val result = codeIdentificationService!!.init()
            if (result.result.resultStatus == "S") {
                // Initialization success.
                logger.info("A+ Server SDK init successful.")
            } else {
                // Block the application startup and troubleshoot
                logger.error("A+ Server SDK init failed.")
                System.exit(0)
            }
        }
    }
}
