package com.task.bankomat.controller;

import com.task.bankomat.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

//
// Данный контроллер обрабатывает запрос пользователя на снятие денег
//

@Controller
public class WithdrawController {

    private final StorageService storageService; // Сервис для работы с хранилищем купюр

    public WithdrawController(StorageService storageService) {
        this.storageService = storageService;
    }

    // При переходе на эту страницу пользователь сможет указать снимаемую сумму (форма в withdraw.mustache)
    @GetMapping("/withdraw")
    public String withdraw(Map<String, Object> model) {

        model.put("banknotes", storageService.getAvailable());// Передача значения для модели
        return "withdraw";
    }
}
