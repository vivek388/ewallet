package com.antgroup.ewallet.service

import com.antgroup.ewallet.model.entity.ApiResult
import com.antgroup.ewallet.model.request.NotifyPaymentRequest
import com.antgroup.ewallet.model.request.UserInitiatedPayRequest
import com.antgroup.ewallet.model.response.UserInitiatedPayResponse
import org.apache.commons.codec.binary.Base64
import org.apache.poi.util.StringUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@Service
class AliPayService(restTemplate: RestTemplate?) {
    @Value("\${alipay.clientId}")
    private val clientId: String? = null

    @Value("\${mpp.privateKey}")
    private val privateKey: String? = null

    @Value("\${alipay.publicKey}")
    private val aliPublicKey: String? = null

    init {
        Companion.restTemplate = restTemplate
    }

    fun getSignature(requestUri: String?, requestTime: String?, requestBody: String?): String {
        val content = String.format("POST %s\n%s.%s.%s", requestUri, clientId, requestTime, requestBody)
        logger.debug("Generating signature with content: {}", content)

        try {
            val signature = Signature.getInstance("SHA256withRSA")
            val priKey = KeyFactory.getInstance("RSA").generatePrivate(
                PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey!!.toByteArray(charset("UTF-8"))))
            )

            signature.initSign(priKey)
            signature.update(content.toByteArray(charset("UTF-8")))

            val signed = signature.sign()
            val encodedSignature = URLEncoder.encode(String(Base64.encodeBase64(signed), charset("UTF-8")), "UTF-8")
            logger.debug("Generated signature: {}", encodedSignature)
            return encodedSignature
        } catch (e: Exception) {
            logger.error("Error generating signature", e)
            throw RuntimeException(e)
        }
    }

    fun verify(
        requestURI: String?,
        responseTime: String?,
        responseBody: String?,
        signatureToBeVerified: String?
    ): Boolean {
        if (StringUtil.isBlank(signatureToBeVerified)) {
            logger.warn("Signature to be verified is blank")
            return false
        }

        val content = String.format("POST %s\n%s.%s.%s", requestURI, clientId, responseTime, responseBody)
        logger.debug("Verifying signature with content: {}", content)

        try {
            val signature = Signature.getInstance("SHA256withRSA")
            val pubKey = KeyFactory.getInstance("RSA").generatePublic(
                X509EncodedKeySpec(Base64.decodeBase64(aliPublicKey!!.toByteArray(charset("UTF-8"))))
            )

            signature.initVerify(pubKey)
            signature.update(content.toByteArray(charset("UTF-8")))

            val isVerified = signature.verify(
                Base64.decodeBase64(
                    URLDecoder.decode(signatureToBeVerified, "UTF-8").toByteArray(
                        charset("UTF-8")
                    )
                )
            )
            logger.debug("Signature verification result: {}", isVerified)
            return isVerified
        } catch (e: Exception) {
            logger.error("Error verifying signature", e)
            throw RuntimeException(e)
        }
    }

    fun getSignatureTest(requestUri: String?, requestTime: String?, requestBody: String?): String {
        val content = String.format("POST %s\n%s.%s.%s", requestUri, clientId, requestTime, requestBody)
        logger.debug("Generated test signature content: {}", content)
        return content
    }

    fun UserInitiatedPay(
        url: String, request: UserInitiatedPayRequest, requestTime: String?,
        signature: String?
    ): UserInitiatedPayResponse? {
        logger.info("Sending User Initiated Pay request to URL: {}", url)
        logger.debug("Request payload: {}", request)
        logger.debug("Request Time: {}, Signature: {}", requestTime, signature)

        val headers = HttpHeaders()
        headers["Client-Id"] = clientId
        headers["Request-Time"] = requestTime
        headers.contentType = MediaType.APPLICATION_JSON
        headers["Signature"] = "algorithm=RSA256,keyVersion=1,signature=$signature"
        headers["markuid"] = "0A"

        val requestEntity = HttpEntity(request, headers)

        try {
            val response = restTemplate!!.exchange(
                url, HttpMethod.POST, requestEntity,
                UserInitiatedPayResponse::class.java
            ).body
            logger.info("Received response from User Initiated Pay API")
            logger.debug("Response: {}", response)
            return response
        } catch (e: Exception) {
            logger.error("Error during User Initiated Pay API call", e)
            throw RuntimeException(e)
        }
    }

    fun NotifyPayment(
        url: String,
        request: NotifyPaymentRequest,
        requestTime: String?,
        signature: String?
    ): ApiResult? {
        logger.info("Sending Notify Payment request to URL: {}", url)
        logger.debug("Request payload: {}", request)
        logger.debug("Request Time: {}, Signature: {}", requestTime, signature)

        val headers = HttpHeaders()
        headers["Client-Id"] = clientId
        headers["Request-Time"] = requestTime
        headers.contentType = MediaType.APPLICATION_JSON
        headers["Signature"] = "algorithm=RSA256,keyVersion=1,signature=$signature"
        headers["markuid"] = "0A"

        val requestEntity = HttpEntity(request, headers)

        try {
            val response = restTemplate!!.exchange(
                url, HttpMethod.POST, requestEntity,
                ApiResult::class.java
            ).body
            logger.info("Received response from Notify Payment API")
            logger.debug("Response: {}", response)
            return response
        } catch (e: Exception) {
            logger.error("Error during Notify Payment API call", e)
            throw RuntimeException(e)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AliPayService::class.java)

        private var restTemplate: RestTemplate? = null
    }
}