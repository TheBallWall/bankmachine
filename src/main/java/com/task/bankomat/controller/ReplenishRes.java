package com.task.bankomat.controller;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.phisical.Acceptor;
import com.task.bankomat.phisical.Printer;
import com.task.bankomat.phisical.Storage;
import com.task.bankomat.repos.AccountRepo;
import com.task.bankomat.repos.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@Controller
public class ReplenishRes {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Storage storage = Storage.INSTANCE;
    private Account userAccount;

    @GetMapping("/replenish_res")
    public String replenish(@RequestParam(name = "num", required = true) String[] strSum, Map<String, Object> model, Principal principal) {
            if (Arrays.asList(strSum).contains("")) {
                //указаны не все купюры
                model.put("message", "Пожалуйста, заполните все поля");
                return "error_user";
            }

        LinkedHashMap<String, Integer> currentLoad = new LinkedHashMap<>(storage.getCurrency());
        int i = 0;
        for (Map.Entry<String, Integer> entry : currentLoad.entrySet()) {
            try {
                currentLoad.replace(entry.getKey(), Integer.parseInt(strSum[i]));
                i++;
            } catch (Exception e) {
                model.put("message", "Необходимо ввести целое число");
                return "error_user";
            }
        }
        Card card = cardRepo.findByNumber(principal.getName());
        userAccount = accountRepo.findById(card.getAccount_id());

        procedure(currentLoad);
        Printer printer = Printer.INSTANCE;
        printer.print();
        model.put("print", "Печать чека");
        model.put("repl", "Ваш счёт пополнен");
        return "replenish_res";
    }

    private void procedure(HashMap<String, Integer> currentLoad) {
        ArrayList<String> chain = new ArrayList<>();
        int sum = 0;
        for (Map.Entry<String, Integer> entry : currentLoad.entrySet()) {
            for (int i = entry.getValue(); i > 0; i--) {
                chain.add(entry.getKey());
                sum += Integer.parseInt(entry.getKey());
            }
        }
        loadMoney(chain, sum);
    }

    private void loadMoney(ArrayList<String> chain, int sum) {
        Acceptor acceptor = Acceptor.INSTANCE;
        for (String i : chain) {
            acceptor.deposit(i);
        }
        userAccount.setAmount(userAccount.getAmount().add(new BigDecimal(sum)));
        accountRepo.save(userAccount);
    }
}
