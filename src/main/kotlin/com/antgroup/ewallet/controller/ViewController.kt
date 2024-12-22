package com.antgroup.ewallet.controller

import com.antgroup.ewallet.service.ExcelService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

@Controller
class ViewController(private val excelService: ExcelService) {
    @GetMapping("/login")
    fun loginPage(request: HttpServletRequest): ModelAndView {
        logger.info("Accessed login page.")
        val modelAndView = ModelAndView()

        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                logger.debug("Checking cookie: {}", cookie.name)
                if (idCookieName == cookie.name) {
                    logger.info("User already logged in, redirecting to wallet page.")
                    modelAndView.viewName = "redirect:/wallet"
                    return modelAndView
                }
            }
        }

        logger.info("No valid session found, rendering login page.")
        modelAndView.viewName = "loginPage"
        return modelAndView
    }

    @GetMapping("/wallet")
    fun walletPage(redirectAttributes: RedirectAttributes, request: HttpServletRequest): ModelAndView {
        logger.info("Accessed wallet page.")
        val modelAndView = ModelAndView()

        val id = getCookieValue(request, idCookieName)
        if (id == null) {
            logger.warn("Session expired. Redirecting to login page.")
            redirectAttributes.addFlashAttribute("error", "Expired, please Login Again")
            modelAndView.viewName = "redirect:/login"
            return modelAndView
        }

        logger.debug("Fetching user details for ID: {}", id)
        val user = excelService.getUserById(id)
        if (user == null) {
            logger.warn("User not found for ID: {}", id)
            redirectAttributes.addFlashAttribute("error", "User not found, please Login Again")
            modelAndView.viewName = "redirect:/login"
            return modelAndView
        }

        val latestTransactions = excelService.getLatestTransactions(id, 3)
        logger.debug("Retrieved latest transactions for user ID {}: {}", id, latestTransactions)

        modelAndView.addObject("latestTransactions", latestTransactions)
        modelAndView.addObject("balance", user.balance)
        modelAndView.viewName = "walletPage"

        logger.info("Wallet page rendered successfully for user ID: {}", id)
        return modelAndView
    }

    @GetMapping("/payment-confirmation")
    fun paymentConfirmationPage(
        redirectAttributes: RedirectAttributes,
        request: HttpServletRequest,
        id: Double
    ): ModelAndView {
        logger.info("Accessed payment confirmation page with payment ID: {}", id)
        val modelAndView = ModelAndView()

        val cId = getCookieValue(request, idCookieName)
        if (cId == null) {
            logger.warn("Session expired. Redirecting to login page.")
            redirectAttributes.addFlashAttribute("error", "Expired, please Login Again")
            modelAndView.viewName = "redirect:/login"
            return modelAndView
        }

        logger.debug("Fetching transaction details for payment ID: {}", id)
        val transaction = excelService.getTransactionByPaymentId(id)
        if (transaction == null) {
            logger.error("Transaction not found for payment ID: {}", id)
            modelAndView.addObject("title", "Something went wrong")
            modelAndView.addObject("titleClass", "fail")
            modelAndView.addObject("status", "Something went wrong")
        } else {
            logger.debug("Transaction found: {}", transaction)
            modelAndView.addObject("title", "Transaction success")
            modelAndView.addObject("titleClass", "balance")

            if (transaction.paymentRequestId == null) {
                modelAndView.addObject("transactionId", transaction.id)
            } else {
                modelAndView.addObject("transactionId", transaction.paymentRequestId)
            }
            modelAndView.addObject("amount", transaction.amount)
            modelAndView.addObject("date", transaction.dateTime)
            modelAndView.addObject("status", transaction.statusCode)

            if (transaction.payCurrency != null) {
                val value = transaction.payAmount?.toDouble()
                if (value != null) {
                    modelAndView.addObject("payment", transaction.payCurrency + value / 100)
                }
            }
            if (transaction.payToCurrency != null) {
                val value = transaction.payToAmount?.toDouble()
                if (value != null) {
                    modelAndView.addObject("payTo", transaction.payToCurrency + value / 100)
                }
            }
            if (transaction.quotePrice != null && transaction.quoteCurrencyPair != null) {
                val currencyPair = "1 " + transaction.quoteCurrencyPair!!.replace("/", " = " + transaction.quotePrice)
                modelAndView.addObject("currency", currencyPair)
            }
        }

        modelAndView.viewName = "paymentConfirmationPage"
        logger.info("Payment confirmation page rendered successfully.")
        return modelAndView
    }

    @GetMapping("/cashier")
    fun cashierPage(redirectAttributes: RedirectAttributes, request: HttpServletRequest): ModelAndView {
        logger.info("Accessed cashier page.")
        val modelAndView = ModelAndView()

        val cId = getCookieValue(request, idCookieName)
        if (cId == null) {
            logger.warn("Session expired. Redirecting to login page.")
            redirectAttributes.addFlashAttribute("error", "Expired, please Login Again")
            modelAndView.viewName = "redirect:/login"
            return modelAndView
        }

        modelAndView.viewName = "cashierPage"
        logger.info("Cashier page rendered successfully.")
        return modelAndView
    }

    private fun getCookieValue(request: HttpServletRequest, cookieName: String): String? {
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookieName == cookie.name) {
                    logger.debug("Found cookie {}: {}", cookieName, cookie.value)
                    return cookie.value
                }
            }
        }
        logger.debug("Cookie {} not found.", cookieName)
        return null
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ViewController::class.java)

        private const val idCookieName = "ewalletID"
    }
}