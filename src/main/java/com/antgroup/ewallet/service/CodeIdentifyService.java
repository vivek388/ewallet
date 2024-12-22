package com.antgroup.ewallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

import com.antgroup.ewallet.model.request.IdentifyCodeRequest;
import com.antgroup.ewallet.model.response.IdentifyCodeResponse;

import sdk.code.model.request.CodeIdentificationRequest;
import sdk.code.model.result.CodeIdentificationResult;
import sdk.code.service.CodeIdentificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CodeIdentifyService {

    private static final Logger logger = LoggerFactory.getLogger(CodeIdentifyService.class);

    @Value("${alipay.clientId}")
    private String clientId;

    private static CodeIdentificationService codeIdentificationService;
    private static RestTemplate restTemplate;

    @Autowired
    public CodeIdentifyService(CodeIdentificationService codeIdentificationService,
                               RestTemplate restTemplate) {
        CodeIdentifyService.codeIdentificationService = codeIdentificationService;
        CodeIdentifyService.restTemplate = restTemplate;
    }

    public CodeIdentificationResult IdentifyCode(String code) {
        logger.info("Starting IdentifyCode for code: {}", code);
        CodeIdentificationRequest request = new CodeIdentificationRequest();
        request.setCodeValue(code);

        CodeIdentificationResult result = codeIdentificationService.identifyCode(request);
        logger.info("CodeIdentificationService result: {}", result);

        if (result.getResult().getResultStatus().equals("S") && result.isSupported()) {
            logger.info("Code is supported with result: {}", result);
            return result;
        }

        if (result.getResult().getResultStatus().equals("F")) {
            logger.warn("Code identification failed with result: {}", result);
            return result;
        }

        logger.warn("IdentifyCode returned null for code: {}", code);
        return null;
    }

    public IdentifyCodeResponse IdentifyCodeViaApi(String url, IdentifyCodeRequest request, String requestTime,
                                                   String signature) {
        logger.info("Starting IdentifyCodeViaApi with URL: {}", url);
        logger.debug("Request payload: {}, Request-Time: {}, Signature: {}", request, requestTime, signature);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Request-Time", requestTime);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Signature", "algorithm=RSA256,keyVersion=1,signature=" + signature);
        headers.set("markuid", "0A");

        logger.debug("Request headers: {}", headers);

        // Create the HttpEntity containing the headers and the payload
        HttpEntity<IdentifyCodeRequest> requestEntity = new HttpEntity<>(request, headers);

        IdentifyCodeResponse response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, IdentifyCodeResponse.class).getBody();
        logger.info("Response received from API: {}", response);

        return response;
    }
}