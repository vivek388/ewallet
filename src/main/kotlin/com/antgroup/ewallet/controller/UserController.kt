package com.antgroup.ewallet.controller

import com.antgroup.ewallet.model.entity.User
import com.antgroup.ewallet.service.ExcelService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class UserController(private val excelService: ExcelService) {
    @get:GetMapping("/users")
    val users: List<User>
        get() {
            logger.info("Fetching all users from the database.")
            val users = excelService.allUserData
            logger.info("Number of users fetched: {}", users.size)
            return users
        }

    @PostMapping("/users/login")
    fun login(
        @RequestParam username: String, @RequestParam password: String,
        redirectAttributes: RedirectAttributes, response: HttpServletResponse
    ): ModelAndView {
        logger.info("Attempting login for username: {}", username)
        val modelAndView = ModelAndView()

        val id = excelService.UserExist(username, password)
        if (id != 0L) {
            logger.info("Login successful for username: {}. User ID: {}", username, id)
            val cookie = Cookie(ID_COOKIE_NAME, id.toString())
            cookie.maxAge = 30 * 24 * 60 * 60 // Set cookie to expire in 1 month
            cookie.path = "/"
            response.addCookie(cookie)
            modelAndView.viewName = "redirect:/wallet"
        } else {
            logger.warn("Login failed for username: {}", username)
            redirectAttributes.addFlashAttribute("error", "Invalid credentials")
            modelAndView.viewName = "redirect:/login"
        }

        return modelAndView
    }

    @PostMapping("/users/logout")
    fun logout(response: HttpServletResponse): ModelAndView {
        logger.info("Logging out the user.")
        val cookie = Cookie(ID_COOKIE_NAME, null)
        cookie.maxAge = 0 // Delete the cookie
        cookie.path = "/"
        response.addCookie(cookie)

        logger.info("User cookie cleared.")
        val modelAndView = ModelAndView()
        modelAndView.viewName = "redirect:/login"
        return modelAndView
    }

    @PostMapping("/users/reload")
    fun reload(@RequestParam("amount") amount: Double, request: HttpServletRequest): ModelAndView {
        logger.info("Processing reload request with amount: {}", amount)

        var id: String = null.toString()
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (ID_COOKIE_NAME == cookie.name) {
                    id = cookie.value
                    break
                }
            }
        }

        if (id == null) {
            logger.error("No valid user ID found in cookies. Reload failed.")
            val errorView = ModelAndView("redirect:/error")
            errorView.addObject("message", "User not authenticated.")
            return errorView
        }

        logger.info("User ID from cookie: {}", id)
        val tranId = excelService.addTransaction(
            id, amount, "Reload", "SUCCESS", "S", "Success",
            null, null, null, null, null, null, null, null, null
        )

        logger.info("Transaction successful. Transaction ID: {}", tranId)
        val modelAndView = ModelAndView()
        modelAndView.viewName = "redirect:/payment-confirmation"
        modelAndView.addObject("id", tranId)
        return modelAndView
    }

    companion object {
        private const val ID_COOKIE_NAME = "ewalletID"
        private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)
    }
}