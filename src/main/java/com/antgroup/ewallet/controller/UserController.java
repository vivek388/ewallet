package com.antgroup.ewallet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.antgroup.ewallet.model.entity.User;
import com.antgroup.ewallet.service.ExcelService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
public class UserController {
    private static final String ID_COOKIE_NAME = "ewalletID";
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ExcelService excelService;

    public UserController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        logger.info("Fetching all users from the database.");
        List<User> users = excelService.getAllUserData();
        logger.info("Number of users fetched: {}", users.size());
        return users;
    }

    @PostMapping("/users/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password,
                              RedirectAttributes redirectAttributes, HttpServletResponse response) {
        logger.info("Attempting login for username: {}", username);
        ModelAndView modelAndView = new ModelAndView();

        long id = excelService.UserExist(username, password);
        if (id != 0) {
            logger.info("Login successful for username: {}. User ID: {}", username, id);
            Cookie cookie = new Cookie(ID_COOKIE_NAME, Long.toString(id));
            cookie.setMaxAge(30 * 24 * 60 * 60); // Set cookie to expire in 1 month
            cookie.setPath("/");
            response.addCookie(cookie);
            modelAndView.setViewName("redirect:/wallet");
        } else {
            logger.warn("Login failed for username: {}", username);
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            modelAndView.setViewName("redirect:/login");
        }

        return modelAndView;
    }

    @PostMapping("/users/logout")
    public ModelAndView logout(HttpServletResponse response) {
        logger.info("Logging out the user.");
        Cookie cookie = new Cookie(ID_COOKIE_NAME, null);
        cookie.setMaxAge(0); // Delete the cookie
        cookie.setPath("/");
        response.addCookie(cookie);

        logger.info("User cookie cleared.");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }

    @PostMapping("/users/reload")
    public ModelAndView reload(@RequestParam("amount") double amount, HttpServletRequest request) {
        logger.info("Processing reload request with amount: {}", amount);

        String id = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ID_COOKIE_NAME.equals(cookie.getName())) {
                    id = cookie.getValue();
                    break;
                }
            }
        }

        if (id == null) {
            logger.error("No valid user ID found in cookies. Reload failed.");
            ModelAndView errorView = new ModelAndView("redirect:/error");
            errorView.addObject("message", "User not authenticated.");
            return errorView;
        }

        logger.info("User ID from cookie: {}", id);
        double tranId = excelService.addTransaction(id, amount, "Reload", "SUCCESS", "S", "Success",
                null, null, null, null, null, null, null, null, null);

        logger.info("Transaction successful. Transaction ID: {}", tranId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/payment-confirmation");
        modelAndView.addObject("id", tranId);
        return modelAndView;
    }
}