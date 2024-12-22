package com.antgroup.ewallet.service

import com.antgroup.ewallet.model.request.IdentifyCodeRequest
import com.antgroup.ewallet.model.response.IdentifyCodeResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import sdk.code.model.request.CodeIdentificationRequest
import sdk.code.model.result.CodeIdentificationResult
import sdk.code.service.CodeIdentificationService

@Service
class CodeIdentifyService @Autowired constructor(
    codeIdentificationService: CodeIdentificationService?,
    restTemplate: RestTemplate?
) {
    @Value("\${alipay.clientId}")
    private val clientId: String? = null

    init {
        Companion.codeIdentificationService = codeIdentificationService
        Companion.restTemplate = restTemplate
    }

    fun IdentifyCode(code: String?): CodeIdentificationResult? {
        logger.info("Starting IdentifyCode for code: {}", code)
        val request = CodeIdentificationRequest()
        request.codeValue = code

        val result = codeIdentificationService!!.identifyCode(request)
        logger.info("CodeIdentificationService result: {}", result)

        if (result.result.resultStatus == "S" && result.isSupported) {
            logger.info("Code is supported with result: {}", result)
            return result
        }

        if (result.result.resultStatus == "F") {
            logger.warn("Code identification failed with result: {}", result)
            return result
        }

        logger.warn("IdentifyCode returned null for code: {}", code)
        return null
    }

    fun IdentifyCodeViaApi(
        url: String, request: IdentifyCodeRequest, requestTime: String?,
        signature: String?
    ): IdentifyCodeResponse? {
        logger.info("Starting IdentifyCodeViaApi with URL: {}", url)
        logger.debug("Request payload: {}, Request-Time: {}, Signature: {}", request, requestTime, signature)

        val headers = HttpHeaders()
        headers["Client-Id"] = clientId
        headers["Request-Time"] = requestTime
        headers.contentType = MediaType.APPLICATION_JSON
        headers["Signature"] = "algorithm=RSA256,keyVersion=1,signature=$signature"
        headers["markuid"] = "0A"

        logger.debug("Request headers: {}", headers)

        // Create the HttpEntity containing the headers and the payload
        val requestEntity = HttpEntity(request, headers)

        val response = restTemplate!!.exchange(
            url, HttpMethod.POST, requestEntity,
            IdentifyCodeResponse::class.java
        ).body
        logger.info("Response received from API: {}", response)

        return response
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CodeIdentifyService::class.java)

        private var codeIdentificationService: CodeIdentificationService? = null
        private var restTemplate: RestTemplate? = null
    }
}