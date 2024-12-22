package com.antgroup.ewallet.controller

import com.antgroup.ewallet.model.request.IdentifyCodeRequest
import com.antgroup.ewallet.model.request.QrCodeRequest
import com.antgroup.ewallet.model.response.IdentifyCodeResponse
import com.antgroup.ewallet.service.AliPayService
import com.antgroup.ewallet.service.CodeIdentifyService
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import sdk.code.model.result.CodeIdentificationResult
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class WalletController(private val aliPayService: AliPayService, private val codeIdentifyService: CodeIdentifyService) {
    @PostMapping("/scan-qr")
    @Throws(JsonProcessingException::class)
    fun scanQr(
        @RequestBody qrCodeRequest: QrCodeRequest, redirectAttributes: RedirectAttributes,
        response: HttpServletResponse
    ): CodeIdentificationResult? {
        logger.info("Received scan-qr request with code: {}", qrCodeRequest.code)

        val result = codeIdentifyService.IdentifyCode(qrCodeRequest.code)

        logger.info("Processed scan-qr request with result: {}", result)
        return result
    }

    @PostMapping("/scan-qr-api")
    @Throws(JsonProcessingException::class)
    fun scanQrApi(
        @RequestBody qrCodeRequest: QrCodeRequest, redirectAttributes: RedirectAttributes,
        response: HttpServletResponse, request: HttpServletRequest
    ): IdentifyCodeResponse? {
        logger.info("Received scan-qr-api request with code: {}", qrCodeRequest.code)

        var id: String = null.toString()
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (idCookieName == cookie.name) {
                    id = cookie.value
                }
            }
        }
        logger.debug("Retrieved customerId from cookies: {}", id)

        val now = LocalDateTime.now()
        val formattedDateTime = now.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'"))
        logger.debug("Formatted current UTC date and time: {}", formattedDateTime)

        val identifyCodeRequest = IdentifyCodeRequest()
        identifyCodeRequest.codeValue = qrCodeRequest.code
        identifyCodeRequest.customerId = id

        val objectMapper = ObjectMapper()
        val identifyCodeJson = objectMapper.writeValueAsString(identifyCodeRequest)
        logger.debug("Constructed IdentifyCodeRequest JSON: {}", identifyCodeJson)

        val signature = aliPayService.getSignature(identifyCodeApiUri, formattedDateTime, identifyCodeJson)
        logger.debug("Generated signature for IdentifyCodeRequest: {}", signature)

        val identifyCodeResp = codeIdentifyService.IdentifyCodeViaApi(
            identifyCodeApiUrl,
            identifyCodeRequest, formattedDateTime, signature
        )

        logger.info("IdentifyCodeResponse received: {}", identifyCodeResp)

        return identifyCodeResp
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WalletController::class.java)

        private const val idCookieName = "ewalletID"
        private const val identifyCodeApiUrl = "https://open-sea-global.alipayplus.com/aps/api/v1/codes/identifyCode"
        private const val identifyCodeApiUri = "/aps/api/v1/codes/identifyCode"
    }
}