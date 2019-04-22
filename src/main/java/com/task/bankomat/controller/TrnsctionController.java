package com.task.bankomat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

//
// Данный контроллер покажет пользователю страницу для перевода средств
//

@Controller
public class TrnsctionController {
    // При переходе на данную страницу пользователь сможет заполнить форму для перевода (transaction.mustache)
    @GetMapping("/transaction")
    public String trans(Map<String, Object> model) {

        return "transaction";
    }
}
