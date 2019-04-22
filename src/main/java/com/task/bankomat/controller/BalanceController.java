package com.task.bankomat.controller;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.repos.AccountRepo;
import com.task.bankomat.repos.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Controller
public class BalanceController {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    @GetMapping("/balance")
    public String greeting (Map<String, Object> model, Principal principal){

        Card card = cardRepo.findByNumber(principal.getName());
        Account account = accountRepo.findById(card.getAccount_id());
        model.put("balance", account.getAmount());
        return "balance";
    }
}
