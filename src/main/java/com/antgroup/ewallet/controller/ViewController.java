package com.antgroup.ewallet.controller;

import com.antgroup.ewallet.model.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.antgroup.ewallet.service.ExcelService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ViewController {

    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

    private static final String idCookieName = "ewalletID";
    private final ExcelService excelService;

    public ViewController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/login")
    public ModelAndView loginPage(HttpServletRequest request) {
        logger.info("Accessed login page.");
        ModelAndView modelAndView = new ModelAndView();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.debug("Checking cookie: {}", cookie.getName());
                if (idCookieName.equals(cookie.getName())) {
                    logger.info("User already logged in, redirecting to wallet page.");
                    modelAndView.setViewName("redirect:/wallet");
                    return modelAndView;
                }
            }
        }

        logger.info("No valid session found, rendering login page.");
        modelAndView.setViewName("loginPage");
        return modelAndView;
    }

    @GetMapping("/wallet")
    public ModelAndView walletPage(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        logger.info("Accessed wallet page.");
        ModelAndView modelAndView = new ModelAndView();

        String id = getCookieValue(request, idCookieName);
        if (id == null) {
            logger.warn("Session expired. Redirecting to login page.");
            redirectAttributes.addFlashAttribute("error", "Expired, please Login Again");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        logger.debug("Fetching user details for ID: {}", id);
        User user = excelService.getUserById(id);
        if (user == null) {
            logger.warn("User not found for ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "User not found, please Login Again");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        List<Transaction> latestTransactions = excelService.getLatestTransactions(id, 3);
        logger.debug("Retrieved latest transactions for user ID {}: {}", id, latestTransactions);

        modelAndView.addObject("latestTransactions", latestTransactions);
        modelAndView.addObject("balance", user.getBalance());
        modelAndView.setViewName("walletPage");

        logger.info("Wallet page rendered successfully for user ID: {}", id);
        return modelAndView;
    }

    @GetMapping("/payment-confirmation")
    public ModelAndView paymentConfirmationPage(RedirectAttributes redirectAttributes, HttpServletRequest request, double id) {
        logger.info("Accessed payment confirmation page with payment ID: {}", id);
        ModelAndView modelAndView = new ModelAndView();

        String cId = getCookieValue(request, idCookieName);
        if (cId == null) {
            logger.warn("Session expired. Redirecting to login page.");
            redirectAttributes.addFlashAttribute("error", "Expired, please Login Again");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        logger.debug("Fetching transaction details for payment ID: {}", id);
        Transaction transaction = excelService.getTransactionByPaymentId(id);
        if (transaction == null) {
            logger.error("Transaction not found for payment ID: {}", id);
            modelAndView.addObject("title", "Something went wrong");
            modelAndView.addObject("titleClass", "fail");
            modelAndView.addObject("status", "Something went wrong");
        } else {
            logger.debug("Transaction found: {}", transaction);
            modelAndView.addObject("title", "Transaction success");
            modelAndView.addObject("titleClass", "balance");

            if (transaction.getPaymentRequestId() == null) {
                modelAndView.addObject("transactionId", transaction.getId());
            } else {
                modelAndView.addObject("transactionId", transaction.getPaymentRequestId());
            }
            modelAndView.addObject("amount", transaction.getAmount());
            modelAndView.addObject("date", transaction.getDateTime());
            modelAndView.addObject("status", transaction.getStatusCode());

            if (transaction.getPayCurrency() != null) {
                double value = Double.parseDouble(transaction.getPayAmount());
                modelAndView.addObject("payment", transaction.getPayCurrency() + value / 100);
            }
            if (transaction.getPayToCurrency() != null) {
                double value = Double.parseDouble(transaction.getPayToAmount());
                modelAndView.addObject("payTo", transaction.getPayToCurrency() + value / 100);
            }
            if (transaction.getQuotePrice() != null && transaction.getQuoteCurrencyPair() != null) {
                String currencyPair = "1 " + transaction.getQuoteCurrencyPair().replace("/", " = " + transaction.getQuotePrice());
                modelAndView.addObject("currency", currencyPair);
            }
        }

        modelAndView.setViewName("paymentConfirmationPage");
        logger.info("Payment confirmation page rendered successfully.");
        return modelAndView;
    }

    @GetMapping("/cashier")
    public ModelAndView cashierPage(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        logger.info("Accessed cashier page.");
        ModelAndView modelAndView = new ModelAndView();

        String cId = getCookieValue(request, idCookieName);
        if (cId == null) {
            logger.warn("Session expired. Redirecting to login page.");
            redirectAttributes.addFlashAttribute("error", "Expired, please Login Again");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        modelAndView.setViewName("cashierPage");
        logger.info("Cashier page rendered successfully.");
        return modelAndView;
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    logger.debug("Found cookie {}: {}", cookieName, cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        logger.debug("Cookie {} not found.", cookieName);
        return null;
    }
}