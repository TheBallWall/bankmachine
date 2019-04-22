package com.task.bankomat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//
// Данный контроллер обрабатывает ошибки вызванные некорректными действиями пользователя
//

@Controller
public class ErrorUserController {
    // При перенапраавлении на данную страницу пользователь увидит сообщение о вызванной ошибке
    @GetMapping("/error_user")
    public String errorInfo(@RequestParam(name = "message", required = false) String message, Map<String, Object> model) {

        model.put("message", message);
        return "error_user";
    }
}
