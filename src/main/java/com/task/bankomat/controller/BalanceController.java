package com.task.bankomat.controller;

import com.task.bankomat.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

//
// Данный контроллер выводит баланс пользователя
//

@Controller
public class BalanceController {

    private final UserDataService userDataService; // Сервис для работы с пользовательской инфоромацией

    @Autowired
    public BalanceController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    // При переходе на данную страницу пользователь получит информацию о своём балансе
    @GetMapping("/balance")
    public String balance(Map<String, Object> model, Principal principal) {

        model.put("balance", userDataService.getBalance(principal.getName())); // Передача значения для модели
        return "balance";
    }
}
