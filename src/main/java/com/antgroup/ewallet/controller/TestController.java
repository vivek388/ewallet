package com.antgroup.ewallet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("test")
    public String Test() {
        logger.info("Handling GET request for '/test' endpoint.");
        String response = "test without param ";
        logger.info("Response: {}", response);
        return response;
    }

    @GetMapping("test-param")
    public String Test(@RequestParam String param) {
        logger.info("Handling GET request for '/test-param' endpoint with param: {}", param);
        String response = "test, param : " + param;
        logger.info("Response: {}", response);
        return response;
    }
}