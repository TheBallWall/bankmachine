package com.task.bankomat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Controller
public class ErrorUserController {
    @GetMapping("/error_user")
    public String errorInfo (@RequestParam(name = "message", required = false) String message, Map<String, Object> model){

        model.put("message",message);
        return "error_user";
    }
}
