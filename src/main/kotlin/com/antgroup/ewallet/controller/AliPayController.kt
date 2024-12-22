package com.antgroup.ewallet.controller

import com.antgroup.ewallet.model.entity.*
import com.antgroup.ewallet.model.request.*
import com.antgroup.ewallet.model.response.InquiryPaymentResponse
import com.antgroup.ewallet.model.response.UserInitiatedPayResponse
import com.antgroup.ewallet.service.AliPayService
import com.antgroup.ewallet.service.ExcelService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
class AliPayController(private val aliPayService: AliPayService, private val excelService: ExcelService) {
    @Value("\${alipay.clientId}")
    private val clientId: String = null.toString()

    @PostMapping("/initiatePay")
    @Throws(JsonProcessingException::class)
    fun initiatePay(@RequestBody qrCodeRequest: QrCodeRequest, request: HttpServletRequest): UserInitiatedPayResponse? {
        logger.info("Received request to initiate payment. QR Code: {}", qrCodeRequest.code)
        var id: String = null.toString()
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (idCookieName == cookie.name) {
                    id = cookie.value
                    break
                }
            }
        }
        logger.debug("Customer ID retrieved from cookie: {}", id)
        val now = LocalDateTime.now()
        val formattedDateTime = now.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"))

        val userInitiatedPayRequest = UserInitiatedPayRequest()

        userInitiatedPayRequest.codeValue = qrCodeRequest.code
        userInitiatedPayRequest.customerId = id

        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val userInitiatedPayJson = objectMapper.writeValueAsString(userInitiatedPayRequest)
        val signature = aliPayService.getSignature(userInitiatedPayApiUri, formattedDateTime, userInitiatedPayJson)
        logger.debug("Generated UserInitiatedPayRequest JSON: {}", userInitiatedPayJson)
        val identifyCodeResp = aliPayService.UserInitiatedPay(
            userInitiatedPayApiUrl,
            userInitiatedPayRequest, formattedDateTime, signature
        )
        logger.debug("Generated API signature for UserInitiatedPay request: {}", signature)

        logger.info("Received UserInitiatedPay response: {}", identifyCodeResp)
        return identifyCodeResp
    }

    @PostMapping("/initiatedPay/pay")
    @Throws(JsonProcessingException::class)
    fun payInitiatedPay(
        @RequestBody initiatedPayResponse: UserInitiatedPayResponse,
        request: HttpServletRequest
    ): WalletApiResult {
        logger.info("Processing payment for response: {}", initiatedPayResponse)
        var id: String = null.toString()
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (idCookieName == cookie.name) {
                    id = cookie.value
                    break
                }
            }
        }
        logger.debug("Customer ID retrieved from cookie: {}", id)

        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val user = excelService.getUserById(id)

        val now = LocalDateTime.now()
        val formattedDateTime = now.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"))

        val payToAmount = initiatedPayResponse.payToAmount!!.value!!.toDouble() / 100
        var paymentId = -1.0
        var apiResult = ApiResult(BaseResult("SUCCESS", "Success", "S"))
        logger.info("Processing initiated payment for user: {} with amount: {}", id, payToAmount)
        if (user?.balance!! < payToAmount) {
            logger.warn(
                "User balance insufficient. User ID: {}, Balance: {}, Required: {}",
                id,
                user.balance,
                payToAmount
            )
            apiResult = ApiResult(BaseResult("USER_BALANCE_NOT_ENOUGH", "Insufficient Balance", "F"))
        } else {
            logger.debug("User balance is sufficient. Proceeding with transaction.")
            var promoJson: String = null.toString()
            if (initiatedPayResponse.paymentPromoInfo != null) {
                promoJson = objectMapper.writeValueAsString(initiatedPayResponse.paymentPromoInfo)
            }

            paymentId = excelService.addTransaction(
                id, -payToAmount, initiatedPayResponse.order!!.orderDescription,
                apiResult.result!!.resultCode, apiResult.result!!.resultStatus, apiResult.result!!.resultMessage,
                initiatedPayResponse.paymentRequestId,
                formattedDateTime, initiatedPayResponse.paymentAmount, initiatedPayResponse.payToAmount,
                initiatedPayResponse.paymentQuote, initiatedPayResponse.pspId, initiatedPayResponse.acquirerId,
                promoJson, null
            )
            logger.info("Transaction recorded. Payment ID: {}", paymentId)
        }

        val notifyRequest = NotifyPaymentRequest(
            apiResult.result,
            initiatedPayResponse.paymentRequestId,
            paymentId.toString(), formattedDateTime, initiatedPayResponse.paymentAmount,
            initiatedPayResponse.payToAmount,
            id
        )

        val notifyPaymentRequestJson = objectMapper.writeValueAsString(notifyRequest)
        logger.debug("Generated NotifyPaymentRequest JSON: {}", notifyPaymentRequestJson)
        val signature = aliPayService.getSignature(notifyPaymentUri, formattedDateTime, notifyPaymentRequestJson)
        logger.debug("Generated API signature for NotifyPayment request: {}", signature)

        val resp = aliPayService.NotifyPayment(
            notifyPaymentUrl,
            notifyRequest, formattedDateTime, signature
        )
        logger.info(
            "Payment result - Status: {}, Message: {}",
            apiResult.result!!.resultCode,
            apiResult.result!!.resultMessage
        )
        return WalletApiResult(resp!!.result, paymentId)
    }

    @PostMapping("/inquiry")
    @Throws(JsonProcessingException::class)
    fun inquiryPayment(
        @RequestBody inquiryRequest: InquiryPaymentRequest,
        @RequestHeader(value = "request-time", required = true) requestTime: String,
        @RequestHeader(value = "signature", required = true) headerSignature: String,
        request: HttpServletRequest
    ): ResponseEntity<InquiryPaymentResponse> {
        logger.info(
            "Received inquiryPayment request: {}, Headers: request-time={}, signature={}",
            inquiryRequest, requestTime, headerSignature
        )

        val respSignature =
            Arrays.stream(headerSignature.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .filter { part: String -> part.startsWith("signature=") }
                .map { part: String -> part.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] }
                .findFirst()
                .orElse(null)

        logger.info("Extracted Signature: {}", respSignature)

        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val inquiryRequestJson = objectMapper.writeValueAsString(inquiryRequest)

        val verified = aliPayService.verify("/inquiry", requestTime, inquiryRequestJson, respSignature)
        logger.info("Signature verification result: {}", verified)

        var baseResult: BaseResult? = null
        var response: InquiryPaymentResponse? = null

        if (!verified) {
            logger.error("Invalid signature for inquiryPayment request.")
            baseResult = BaseResult("INVALID_SIGNATURE", "The signature is invalid.", "F")
            response = InquiryPaymentResponse(null, null, null, null, null, null, baseResult)
        } else {
            val transactions = excelService.getAlipayTransactions(
                inquiryRequest.paymentRequestId,
                inquiryRequest.pspId, inquiryRequest.acquirerId
            )
            logger.info(
                "Fetched transactions for paymentRequestId={}, pspId={}, acquirerId={}: {}",
                inquiryRequest.paymentRequestId, inquiryRequest.pspId, inquiryRequest.acquirerId, transactions
            )

            if (transactions == null || transactions.size <= 0) {
                baseResult = BaseResult("ORDER_NOT_EXIST", "The order doesn't exist.", "F")
                response = InquiryPaymentResponse(null, null, null, null, null, null, baseResult)
            } else {
                val transaction = transactions.stream()
                    .sorted { t1: Transaction, t2: Transaction -> t1.dateTime.compareTo(t2.dateTime) }
                    .findFirst().get()
                logger.info("Selected transaction for inquiry response: {}", transaction)

                baseResult = BaseResult(
                    transaction.statusCode, transaction.statusMessage,
                    transaction.status
                )
                val payToAmount = BasePayment(transaction.payToAmount, transaction.payToCurrency)
                val paymentAmount = BasePayment(transaction.payAmount, transaction.payCurrency)

                response = InquiryPaymentResponse(
                    transaction.customerId,
                    payToAmount, paymentAmount, transaction.id.toString(),
                    baseResult, transaction.paymentTime, BaseResult(true)
                )
            }
        }

        val now = LocalDateTime.now()
        val formattedDateTime = now.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"))

        val resultJson = objectMapper.writeValueAsString(response)
        val signature = aliPayService.getSignature("/inquiry", formattedDateTime, resultJson)
        logger.info("Generated response signature: {}", signature)

        // Read headers for log
        val headersInfo = StringBuilder()
        request.headerNames.asIterator().forEachRemaining { headerName: String ->
            headersInfo.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n")
        }
        logger.debug("Request headers: \n{}", headersInfo)

        excelService.addApiCallLog(
            "/inquiry",
            "Body : $inquiryRequestJson\nHeaders: \n$headersInfo",
            resultJson, verified
        )

        logger.info("Sending inquiryPayment response: {}", response)
        return ResponseEntity.ok()
            .header("Signature", "algorithm=RSA256,keyVersion=1,signature=$signature")
            .header("Response-Time", formattedDateTime)
            .header("Client-Id", clientId)
            .body(response)
    }

    @PostMapping("/cancel")
    @Throws(JsonProcessingException::class)
    fun cancelPayment(
        @RequestBody cancelRequest: CancelRequest,
        @RequestHeader(value = "request-time", required = true) requestTime: String,
        @RequestHeader(value = "signature", required = true) headerSignature: String,
        request: HttpServletRequest
    ): ResponseEntity<ApiResult> {
        logger.info(
            "Received cancelPayment request: {}, Headers: request-time={}, signature={}",
            cancelRequest, requestTime, headerSignature
        )

        val respSignature =
            Arrays.stream(headerSignature.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .filter { part: String -> part.startsWith("signature=") }
                .map { part: String -> part.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] }
                .findFirst()
                .orElse(null)

        logger.info("Extracted Signature: {}", respSignature)

        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val cancelRequestJson = objectMapper.writeValueAsString(cancelRequest)

        val verified = aliPayService.verify("/cancel", requestTime, cancelRequestJson, respSignature)
        logger.info("Signature verification result: {}", verified)

        var apiResult: ApiResult? = null

        if (!verified) {
            logger.error("Invalid signature for cancelPayment request.")
            apiResult = ApiResult(BaseResult("INVALID_SIGNATURE", "The signature is invalid.", "F"))
        } else {
            val transactions = excelService.getAlipayTransactions(
                cancelRequest.paymentRequestId,
                cancelRequest.pspId, cancelRequest.acquirerId
            )

            logger.info(
                "Fetched transactions for paymentRequestId={}, pspId={}, acquirerId={}: {}",
                cancelRequest.paymentRequestId, cancelRequest.pspId, cancelRequest.acquirerId, transactions
            )

            if (transactions == null || transactions.size == 0) {
                logger.warn("No transactions found for cancelPayment request.")
                apiResult = ApiResult(BaseResult("ORDER_NOT_EXIST", "The order doesn't exist.", "F"))
            } else {
                val transaction = transactions.stream()
                    .sorted { t1: Transaction, t2: Transaction -> t1.dateTime.compareTo(t2.dateTime) }
                    .findFirst().get()
                logger.info("Selected transaction for cancellation: {}", transaction)

                if (transaction.statusCode != orderIsClosed) {
                    logger.info("Cancelling transaction for paymentRequestId={}", cancelRequest.paymentRequestId)
                    excelService.cancelTransaction(cancelRequest.paymentRequestId)
                }

                apiResult = ApiResult(BaseResult(true))
            }
        }

        val now = LocalDateTime.now()
        val formattedDateTime = now.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"))

        val resultJson = objectMapper.writeValueAsString(apiResult)
        val signature = aliPayService.getSignature("/cancel", formattedDateTime, resultJson)
        logger.info("Generated response signature: {}", signature)

        // Read headers for log
        val headersInfo = StringBuilder()
        request.headerNames.asIterator().forEachRemaining { headerName: String ->
            headersInfo.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n")
        }
        logger.debug("Request headers: \n{}", headersInfo)

        excelService.addApiCallLog(
            "/cancel", "Body : $cancelRequestJson\nHeaders: \n$headersInfo",
            resultJson, verified
        )

        logger.info("Sending cancelPayment response: {}", apiResult)
        return ResponseEntity.ok()
            .header("Signature", "algorithm=RSA256,keyVersion=1,signature=$signature")
            .header("Response-Time", formattedDateTime)
            .header("Client-Id", clientId)
            .body(apiResult)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
        private const val idCookieName = "ewalletID"
        private const val userInitiatedPayApiUrl =
            "https://open-sea-global.alipayplus.com/aps/api/v1/payments/userInitiatedPay"
        private const val userInitiatedPayApiUri = "/aps/api/v1/payments/userInitiatedPay"
        private const val notifyPaymentUrl = "https://open-sea-global.alipayplus.com/aps/api/v1/payments/notifyPayment"
        private const val notifyPaymentUri = "/aps/api/v1/payments/notifyPayment"
        private const val orderIsClosed = "ORDER_IS_CLOSED"
    }
}