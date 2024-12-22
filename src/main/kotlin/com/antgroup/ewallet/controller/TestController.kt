package com.antgroup.ewallet.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("test")
    fun Test(): String {
        logger.info("Handling GET request for '/test' endpoint.")
        val response = "test without param "
        logger.info("Response: {}", response)
        return response
    }

    @GetMapping("test-param")
    fun Test(@RequestParam param: String): String {
        logger.info("Handling GET request for '/test-param' endpoint with param: {}", param)
        val response = "test, param : $param"
        logger.info("Response: {}", response)
        return response
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TestController::class.java)
    }
}