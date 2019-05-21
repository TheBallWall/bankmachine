package com.task.bankomat.controller;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.repos.AccountRepo;
import com.task.bankomat.repos.CardRepo;
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
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    // При переходе на данную страницу пользователь получит информацию о соём балансе
    @GetMapping("/balance")
    public String balance(Map<String, Object> model, Principal principal) {

        Card card = cardRepo.findByNumber(principal.getName()); // Получение информации из бд о карточке пользователя
        Account account = accountRepo.findById(card.getAccountId()); // Получение информации из бд об аккаунте пользователя
        model.put("balance", account.getAmount()); // Передача значения для модели
        return "balance";
    }
}
