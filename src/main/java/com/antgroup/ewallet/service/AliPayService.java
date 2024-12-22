package com.antgroup.ewallet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antgroup.ewallet.model.entity.*;
import com.antgroup.ewallet.model.request.*;
import com.antgroup.ewallet.model.response.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.poi.util.StringUtil;
import org.apache.commons.codec.binary.Base64;

@Service
public class AliPayService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayService.class);

    @Value("${alipay.clientId}")
    private String clientId;

    @Value("${mpp.privateKey}")
    private String privateKey;

    @Value("${alipay.publicKey}")
    private String aliPublicKey;

    private static RestTemplate restTemplate;

    public AliPayService(RestTemplate restTemplate) {
        AliPayService.restTemplate = restTemplate;
    }

    public String getSignature(String requestUri, String requestTime, String requestBody) {
        String content = String.format("POST %s\n%s.%s.%s", requestUri, clientId, requestTime, requestBody);
        logger.debug("Generating signature with content: {}", content);

        try {
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes("UTF-8"))));

            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));

            byte[] signed = signature.sign();
            String encodedSignature = URLEncoder.encode(new String(Base64.encodeBase64(signed), "UTF-8"), "UTF-8");
            logger.debug("Generated signature: {}", encodedSignature);
            return encodedSignature;
        } catch (Exception e) {
            logger.error("Error generating signature", e);
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String requestURI, String responseTime, String responseBody, String signatureToBeVerified) {
        if (StringUtil.isBlank(signatureToBeVerified)) {
            logger.warn("Signature to be verified is blank");
            return false;
        }

        String content = String.format("POST %s\n%s.%s.%s", requestURI, clientId, responseTime, responseBody);
        logger.debug("Verifying signature with content: {}", content);

        try {
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.decodeBase64(aliPublicKey.getBytes("UTF-8"))));

            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));

            boolean isVerified = signature.verify(Base64.decodeBase64(URLDecoder.decode(signatureToBeVerified, "UTF-8").getBytes("UTF-8")));
            logger.debug("Signature verification result: {}", isVerified);
            return isVerified;
        } catch (Exception e) {
            logger.error("Error verifying signature", e);
            throw new RuntimeException(e);
        }
    }

    public String getSignatureTest(String requestUri, String requestTime, String requestBody) {
        String content = String.format("POST %s\n%s.%s.%s", requestUri, clientId, requestTime, requestBody);
        logger.debug("Generated test signature content: {}", content);
        return content;
    }

    public UserInitiatedPayResponse UserInitiatedPay(String url, UserInitiatedPayRequest request, String requestTime,
                                                     String signature) {
        logger.info("Sending User Initiated Pay request to URL: {}", url);
        logger.debug("Request payload: {}", request);
        logger.debug("Request Time: {}, Signature: {}", requestTime, signature);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Request-Time", requestTime);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Signature", "algorithm=RSA256,keyVersion=1,signature=" + signature);
        headers.set("markuid", "0A");

        HttpEntity<UserInitiatedPayRequest> requestEntity = new HttpEntity<>(request, headers);

        try {
            UserInitiatedPayResponse response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, UserInitiatedPayResponse.class).getBody();
            logger.info("Received response from User Initiated Pay API");
            logger.debug("Response: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("Error during User Initiated Pay API call", e);
            throw new RuntimeException(e);
        }
    }

    public ApiResult NotifyPayment(String url, NotifyPaymentRequest request, String requestTime, String signature) {
        logger.info("Sending Notify Payment request to URL: {}", url);
        logger.debug("Request payload: {}", request);
        logger.debug("Request Time: {}, Signature: {}", requestTime, signature);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Request-Time", requestTime);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Signature", "algorithm=RSA256,keyVersion=1,signature=" + signature);
        headers.set("markuid", "0A");

        HttpEntity<NotifyPaymentRequest> requestEntity = new HttpEntity<>(request, headers);

        try {
            ApiResult response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResult.class).getBody();
            logger.info("Received response from Notify Payment API");
            logger.debug("Response: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("Error during Notify Payment API call", e);
            throw new RuntimeException(e);
        }
    }
}