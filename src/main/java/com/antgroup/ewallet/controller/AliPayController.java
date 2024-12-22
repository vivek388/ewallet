package com.antgroup.ewallet.controller;

import com.antgroup.ewallet.model.entity.*;
import com.antgroup.ewallet.model.request.*;
import com.antgroup.ewallet.model.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.antgroup.ewallet.service.AliPayService;
import com.antgroup.ewallet.service.ExcelService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
public class AliPayController {
    @Value("${alipay.clientId}")
    private String clientId;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static String idCookieName = "ewalletID";
    private static String userInitiatedPayApiUrl = "https://open-sea-global.alipayplus.com/aps/api/v1/payments/userInitiatedPay";
    private static String userInitiatedPayApiUri = "/aps/api/v1/payments/userInitiatedPay";
    private static String notifyPaymentUrl = "https://open-sea-global.alipayplus.com/aps/api/v1/payments/notifyPayment";
    private static String notifyPaymentUri = "/aps/api/v1/payments/notifyPayment";
    private static String orderIsClosed = "ORDER_IS_CLOSED";

    private AliPayService aliPayService;
    private ExcelService excelService;

    public AliPayController(AliPayService aliPayService, ExcelService excelService) {
        this.aliPayService = aliPayService;
        this.excelService = excelService;
    }
    @PostMapping("/initiatePay")
    public UserInitiatedPayResponse initiatePay(@RequestBody QrCodeRequest qrCodeRequest, HttpServletRequest request)

            throws JsonProcessingException {
        logger.info("Received request to initiate payment. QR Code: {}", qrCodeRequest.getCode());
        String id = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (idCookieName.equals(cookie.getName())) {
                    id = cookie.getValue();
                    break;
                }
            }
        }
        logger.debug("Customer ID retrieved from cookie: {}", id);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));

        UserInitiatedPayRequest userInitiatedPayRequest = new UserInitiatedPayRequest();

        userInitiatedPayRequest.setCodeValue(qrCodeRequest.getCode());
        userInitiatedPayRequest.setCustomerId(id);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String userInitiatedPayJson = objectMapper.writeValueAsString(userInitiatedPayRequest);
        String signature = aliPayService.getSignature(userInitiatedPayApiUri, formattedDateTime, userInitiatedPayJson);
        logger.debug("Generated UserInitiatedPayRequest JSON: {}", userInitiatedPayJson);
        UserInitiatedPayResponse identifyCodeResp = aliPayService.UserInitiatedPay(userInitiatedPayApiUrl,
                userInitiatedPayRequest, formattedDateTime, signature);
        logger.debug("Generated API signature for UserInitiatedPay request: {}", signature);

        logger.info("Received UserInitiatedPay response: {}", identifyCodeResp);
        return identifyCodeResp;
    }

    @PostMapping("/initiatedPay/pay")
    public WalletApiResult payInitiatedPay(@RequestBody UserInitiatedPayResponse initiatedPayResponse,
                                           HttpServletRequest request) throws JsonProcessingException {
        logger.info("Processing payment for response: {}", initiatedPayResponse);
        String id = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (idCookieName.equals(cookie.getName())) {
                    id = cookie.getValue();
                    break;
                }
            }
        }
        logger.debug("Customer ID retrieved from cookie: {}", id);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        User user = excelService.getUserById(id);

        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));

        double payToAmount = Double.parseDouble(initiatedPayResponse.payToAmount.value) / 100;
        double paymentId = -1;
        ApiResult apiResult = new ApiResult(new BaseResult("SUCCESS", "Success", "S"));
        logger.info("Processing initiated payment for user: {} with amount: {}", id, payToAmount);
        if (user.getBalance() < payToAmount) {
            logger.warn("User balance insufficient. User ID: {}, Balance: {}, Required: {}", id, user.getBalance(), payToAmount);
            apiResult = new ApiResult(new BaseResult("USER_BALANCE_NOT_ENOUGH", "Insufficient Balance", "F"));
        } else {
            logger.debug("User balance is sufficient. Proceeding with transaction.");
            String promoJson = null;
            if (initiatedPayResponse.paymentPromoInfo != null) {
                promoJson = objectMapper.writeValueAsString(initiatedPayResponse.paymentPromoInfo);
            }

            paymentId = excelService.addTransaction(id, -payToAmount, initiatedPayResponse.order.orderDescription,
                    apiResult.result.resultCode, apiResult.result.resultStatus, apiResult.result.resultMessage,
                    initiatedPayResponse.paymentRequestId,
                    formattedDateTime, initiatedPayResponse.paymentAmount, initiatedPayResponse.payToAmount,
                    initiatedPayResponse.paymentQuote, initiatedPayResponse.pspId, initiatedPayResponse.acquirerId,
                    promoJson, null);
            logger.info("Transaction recorded. Payment ID: {}", paymentId);
        }

        NotifyPaymentRequest notifyRequest = new NotifyPaymentRequest(apiResult.result,
                initiatedPayResponse.paymentRequestId,
                Double.toString(paymentId), formattedDateTime, initiatedPayResponse.paymentAmount,
                initiatedPayResponse.payToAmount,
                id);

        String notifyPaymentRequestJson = objectMapper.writeValueAsString(notifyRequest);
        logger.debug("Generated NotifyPaymentRequest JSON: {}", notifyPaymentRequestJson);
        String signature = aliPayService.getSignature(notifyPaymentUri, formattedDateTime, notifyPaymentRequestJson);
        logger.debug("Generated API signature for NotifyPayment request: {}", signature);

        ApiResult resp = aliPayService.NotifyPayment(notifyPaymentUrl,
                notifyRequest, formattedDateTime, signature);
        logger.info("Payment result - Status: {}, Message: {}", apiResult.result.resultCode, apiResult.result.resultMessage);
        return new WalletApiResult(resp.result, paymentId);
    }

    @PostMapping("/inquiry")
    public ResponseEntity<InquiryPaymentResponse> inquiryPayment(@RequestBody InquiryPaymentRequest inquiryRequest,
                                                                 @RequestHeader(value = "request-time", required = true) String requestTime,
                                                                 @RequestHeader(value = "signature", required = true) String headerSignature,
                                                                 HttpServletRequest request) throws JsonProcessingException {
        logger.info("Received inquiryPayment request: {}, Headers: request-time={}, signature={}",
                inquiryRequest, requestTime, headerSignature);

        String respSignature = Arrays.stream(headerSignature.split(","))
                .filter(part -> part.startsWith("signature="))
                .map(part -> part.split("=")[1])
                .findFirst()
                .orElse(null);

        logger.info("Extracted Signature: {}", respSignature);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String inquiryRequestJson = objectMapper.writeValueAsString(inquiryRequest);

        boolean verified = aliPayService.verify("/inquiry", requestTime, inquiryRequestJson, respSignature);
        logger.info("Signature verification result: {}", verified);

        BaseResult baseResult = null;
        InquiryPaymentResponse response = null;

        if (!verified) {
            logger.error("Invalid signature for inquiryPayment request.");
            baseResult = new BaseResult("INVALID_SIGNATURE", "The signature is invalid.", "F");
            response = new InquiryPaymentResponse(null, null, null, null, null, null, baseResult);
        } else {
            List<Transaction> transactions = excelService.getAlipayTransactions(inquiryRequest.paymentRequestId,
                    inquiryRequest.pspId, inquiryRequest.acquirerId);
            logger.info("Fetched transactions for paymentRequestId={}, pspId={}, acquirerId={}: {}",
                    inquiryRequest.paymentRequestId, inquiryRequest.pspId, inquiryRequest.acquirerId, transactions);

            if (transactions == null || transactions.size() <= 0) {
                baseResult = new BaseResult("ORDER_NOT_EXIST", "The order doesn't exist.", "F");
                response = new InquiryPaymentResponse(null, null, null, null, null, null, baseResult);
            } else {
                Transaction transaction = transactions.stream()
                        .sorted((t1, t2) -> t1.getDateTime().compareTo(t2.getDateTime()))
                        .findFirst().get();
                logger.info("Selected transaction for inquiry response: {}", transaction);

                baseResult = new BaseResult(transaction.getStatusCode(), transaction.getStatusMessage(),
                        transaction.getStatus());
                BasePayment payToAmount = new BasePayment(transaction.getPayToAmount(), transaction.getPayToCurrency());
                BasePayment paymentAmount = new BasePayment(transaction.getPayAmount(), transaction.getPayCurrency());

                response = new InquiryPaymentResponse(transaction.getCustomerId(),
                        payToAmount, paymentAmount, Double.toString(transaction.getId()),
                        baseResult, transaction.getPaymentTime(), new BaseResult(true));
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));

        String resultJson = objectMapper.writeValueAsString(response);
        String signature = aliPayService.getSignature("/inquiry", formattedDateTime, resultJson);
        logger.info("Generated response signature: {}", signature);

        // Read headers for log
        StringBuilder headersInfo = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headersInfo.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        });
        logger.debug("Request headers: \n{}", headersInfo);

        excelService.addApiCallLog("/inquiry",
                "Body : " + inquiryRequestJson + "\nHeaders: \n" + headersInfo.toString(),
                resultJson, verified);

        logger.info("Sending inquiryPayment response: {}", response);
        return ResponseEntity.ok()
                .header("Signature", "algorithm=RSA256,keyVersion=1,signature=" + signature)
                .header("Response-Time", formattedDateTime)
                .header("Client-Id", clientId)
                .body(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResult> cancelPayment(@RequestBody CancelRequest cancelRequest,
                                                   @RequestHeader(value = "request-time", required = true) String requestTime,
                                                   @RequestHeader(value = "signature", required = true) String headerSignature,
                                                   HttpServletRequest request) throws JsonProcessingException {
        logger.info("Received cancelPayment request: {}, Headers: request-time={}, signature={}",
                cancelRequest, requestTime, headerSignature);

        String respSignature = Arrays.stream(headerSignature.split(","))
                .filter(part -> part.startsWith("signature="))
                .map(part -> part.split("=")[1])
                .findFirst()
                .orElse(null);

        logger.info("Extracted Signature: {}", respSignature);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String cancelRequestJson = objectMapper.writeValueAsString(cancelRequest);

        boolean verified = aliPayService.verify("/cancel", requestTime, cancelRequestJson, respSignature);
        logger.info("Signature verification result: {}", verified);

        ApiResult apiResult = null;

        if (!verified) {
            logger.error("Invalid signature for cancelPayment request.");
            apiResult = new ApiResult(new BaseResult("INVALID_SIGNATURE", "The signature is invalid.", "F"));
        } else {
            List<Transaction> transactions = excelService.getAlipayTransactions(cancelRequest.paymentRequestId,
                    cancelRequest.pspId, cancelRequest.acquirerId);

            logger.info("Fetched transactions for paymentRequestId={}, pspId={}, acquirerId={}: {}",
                    cancelRequest.paymentRequestId, cancelRequest.pspId, cancelRequest.acquirerId, transactions);

            if (transactions == null || transactions.size() == 0) {
                logger.warn("No transactions found for cancelPayment request.");
                apiResult = new ApiResult(new BaseResult("ORDER_NOT_EXIST", "The order doesn't exist.", "F"));
            } else {
                Transaction transaction = transactions.stream()
                        .sorted((t1, t2) -> t1.getDateTime().compareTo(t2.getDateTime()))
                        .findFirst().get();
                logger.info("Selected transaction for cancellation: {}", transaction);

                if (!transaction.getStatusCode().equals(orderIsClosed)) {
                    logger.info("Cancelling transaction for paymentRequestId={}", cancelRequest.paymentRequestId);
                    excelService.cancelTransaction(cancelRequest.paymentRequestId);
                }

                apiResult = new ApiResult(new BaseResult(true));
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));

        String resultJson = objectMapper.writeValueAsString(apiResult);
        String signature = aliPayService.getSignature("/cancel", formattedDateTime, resultJson);
        logger.info("Generated response signature: {}", signature);

        // Read headers for log
        StringBuilder headersInfo = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headersInfo.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        });
        logger.debug("Request headers: \n{}", headersInfo);

        excelService.addApiCallLog("/cancel", "Body : " + cancelRequestJson + "\nHeaders: \n" + headersInfo.toString(),
                resultJson, verified);

        logger.info("Sending cancelPayment response: {}", apiResult);
        return ResponseEntity.ok()
                .header("Signature", "algorithm=RSA256,keyVersion=1,signature=" + signature)
                .header("Response-Time", formattedDateTime)
                .header("Client-Id", clientId)
                .body(apiResult);
    }
}