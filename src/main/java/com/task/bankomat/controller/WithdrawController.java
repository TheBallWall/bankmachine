package com.task.bankomat.controller;

import com.task.bankomat.phisical.Storage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Map;

//
// Данный контроллер обрабатывает запрос пользователя на снятие денег
//

@Controller
public class WithdrawController {

    private Storage storage = Storage.INSTANCE;

    // При переходе на эту страницу пользователь сможет указать снимаемую сумму (форма в withdraw.mustache)
    @GetMapping("/withdraw")
    public String withdraw(Map<String, Object> model) {
        // Последующая логика позволяет передать список доступных купюр на страницу
        ArrayList<String> available = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : storage.getCurrency().entrySet()) { // Проверяя все элементы находящиеся в хранилище, заполняется список доступных купюр
            if (entry.getValue() > 0) {
                available.add(entry.getKey());
            }
        }
        model.put("banknotes", available);// Передача значения для модели
        return "withdraw";
    }
}
