package com.antgroup.ewallet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.antgroup.ewallet.model.request.IdentifyCodeRequest;
import com.antgroup.ewallet.model.response.IdentifyCodeResponse;
import com.antgroup.ewallet.model.request.QrCodeRequest;
import com.antgroup.ewallet.service.AliPayService;
import com.antgroup.ewallet.service.CodeIdentifyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sdk.code.model.result.CodeIdentificationResult;

@RestController
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private static String idCookieName = "ewalletID";
    private static String identifyCodeApiUrl = "https://open-sea-global.alipayplus.com/aps/api/v1/codes/identifyCode";
    private static String identifyCodeApiUri = "/aps/api/v1/codes/identifyCode";

    private AliPayService aliPayService;
    private CodeIdentifyService codeIdentifyService;

    public WalletController(AliPayService aliPayService, CodeIdentifyService codeIdentifyService) {
        this.aliPayService = aliPayService;
        this.codeIdentifyService = codeIdentifyService;
    }

    @PostMapping("/scan-qr")
    public CodeIdentificationResult scanQr(@RequestBody QrCodeRequest qrCodeRequest, RedirectAttributes redirectAttributes,
                                           HttpServletResponse response) throws JsonProcessingException {
        logger.info("Received scan-qr request with code: {}", qrCodeRequest.getCode());

        CodeIdentificationResult result = codeIdentifyService.IdentifyCode(qrCodeRequest.getCode());

        logger.info("Processed scan-qr request with result: {}", result);
        return result;
    }

    @PostMapping("/scan-qr-api")
    public IdentifyCodeResponse scanQrApi(@RequestBody QrCodeRequest qrCodeRequest, RedirectAttributes redirectAttributes,
                                          HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        logger.info("Received scan-qr-api request with code: {}", qrCodeRequest.getCode());

        String id = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (idCookieName.equals(cookie.getName())) {
                    id = cookie.getValue();
                }
            }
        }
        logger.debug("Retrieved customerId from cookies: {}", id);

        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
        logger.debug("Formatted current UTC date and time: {}", formattedDateTime);

        IdentifyCodeRequest identifyCodeRequest = new IdentifyCodeRequest();
        identifyCodeRequest.setCodeValue(qrCodeRequest.getCode());
        identifyCodeRequest.setCustomerId(id);

        ObjectMapper objectMapper = new ObjectMapper();
        String identifyCodeJson = objectMapper.writeValueAsString(identifyCodeRequest);
        logger.debug("Constructed IdentifyCodeRequest JSON: {}", identifyCodeJson);

        String signature = aliPayService.getSignature(identifyCodeApiUri, formattedDateTime, identifyCodeJson);
        logger.debug("Generated signature for IdentifyCodeRequest: {}", signature);

        IdentifyCodeResponse identifyCodeResp = codeIdentifyService.IdentifyCodeViaApi(identifyCodeApiUrl,
                identifyCodeRequest, formattedDateTime, signature);

        logger.info("IdentifyCodeResponse received: {}", identifyCodeResp);

        return identifyCodeResp;
    }
}