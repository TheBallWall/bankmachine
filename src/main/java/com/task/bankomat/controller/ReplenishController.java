package com.task.bankomat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

//
// Данный контроллер обрабатывает запрос пользователя на внесение денег
//

@Controller
public class ReplenishController {
    // При переходе на эту страницу пользователь сможет указать вносимые номиналы купюр (форма в replenish.mustache)
    @GetMapping("/replenish")
    public String trans(Map<String, Object> model) {

        return "replenish";
    }
}

