package com.task.bankomat.controller;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
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
import java.util.Map;

@Controller
public class TrnsctionResController {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Account userAccount;

    @GetMapping("/transaction_res")
    public String trans(@RequestParam(name = "num", required = true) String strNum, @RequestParam(name = "sum", required = true) String strSum, Map<String, Object> model, Principal principal){
        BigDecimal sum;
        try{
            sum = new BigDecimal(strSum);
        }
        catch (Exception e){
            model.put("message","Необходимо ввести число");
            return "error_user";
        }
        Account recipientAccount = accountRepo.findByNumber(strNum);

        if(recipientAccount == null || recipientAccount.getStatus_id() != 1){
            model.put("message", "Перевод на данный счёт невозможен");
            return "error_user";
        }

        Card card = cardRepo.findByNumber(principal.getName());
        userAccount = accountRepo.findById(card.getAccount_id());

        BigDecimal userMoney = new BigDecimal(userAccount.getAmount().toString());

        if (userMoney.compareTo(sum) < 0) {
            //сумма больше, чем денег на счёте
            model.put("message", "На счёте недостаточно средств");
            return "error_user";
        }
        procedure(recipientAccount,sum);

        Printer printer = Printer.INSTANCE;
        printer.print();
        model.put("print", "Печать чека");
        model.put("trans", "Перевод совершён");

        return "transaction_res";
    }
    private void procedure(Account rA,BigDecimal sum){
        userAccount.setAmount(userAccount.getAmount().subtract(sum));
        rA.setAmount(rA.getAmount().add(sum));
        accountRepo.save(userAccount);
        accountRepo.save(rA);
    }
}