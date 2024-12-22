package com.antgroup.ewallet.controller

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, model: Model?): ModelAndView {
        // model.addAttribute("errorMessage", e.getMessage());
        // return "error"; // Return the error view

        val modelAndView = ModelAndView()
        modelAndView.viewName = "error"
        modelAndView.addObject("errorMessage", e.message)
        return modelAndView
    }
}