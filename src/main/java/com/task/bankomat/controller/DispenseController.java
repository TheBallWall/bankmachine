package com.task.bankomat.controller;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.phisical.Dispenser;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DispenseController {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Storage storage = Storage.INSTANCE;
    private Account userAccount;

    @GetMapping("/dispense")
    public String post(@RequestParam(name = "sum", required = true) String strSum, Map<String, Object> model, Principal principal) {

        int sum;
        try{
            sum = Integer.parseInt(strSum);
        }
        catch (Exception e){
            model.put("message","Необходимо ввести число");
            return "error_user";
        }
        Card card = cardRepo.findByNumber(principal.getName());
        userAccount = accountRepo.findById(card.getAccount_id());

        BigDecimal userMoney = new BigDecimal(userAccount.getAmount().toString());

        if (chkSum(sum, userMoney) == 0 || procedure(sum) == 0) {
            // ошибка требуемой суммы
            model.put("message", "Автомат не может выдать данную сумму");
            return "error_user";
        }

        Printer printer = Printer.INSTANCE;
        printer.print();
        model.put("print", "Печать чека");
        model.put("dispense", "Деньги выданы");
        return "dispense";
    }

    private int chkSum(int sum, BigDecimal uM) {
        if (sum > 40000) {
            //сумма должна быть меньше 40000
            return 0;
        }
        if (uM.compareTo(new BigDecimal(sum)) < 0) {
            //сумма больше, чем денег на счёте
            return 0;
        }
        if (sum % findLowest() != 0) {
            //сумма обязана делиться без остатка на наименьшую имеющуюся купюру 
            return 0;
        }
        if (sum == 0){
            //нельзя выдавать 0
            return 0;
        }
        return 1;
    }

    private int procedure(int sum) {

        int currentSum = sum;
        LinkedHashMap<String, Integer> currentStorage = new LinkedHashMap<>(storage.getCurrency());
        ArrayList<String> chain = new ArrayList<>();
        ArrayList<String> keyList = new ArrayList<>(currentStorage.keySet());
        for (int i = keyList.size() - 1; i >= 0 && currentSum > 0; i--) {
            int a = Integer.parseInt(keyList.get(i));
            if (currentSum >= a && currentStorage.get(keyList.get(i)) > 0) {
                currentSum -= a;
                currentStorage.replace(keyList.get(i), currentStorage.get(keyList.get(i)), currentStorage.get(keyList.get(i)) - 1);
                chain.add(keyList.get(i));
                i++;
            }
        }
        if (currentSum == 0 && chain.size() <= 40) {
            throwCash(chain,sum);
        } else {
            // невозможно выдать сумму
            return 0;
        }

        return 1;
    }

    private void throwCash(ArrayList<String> s, int sum) {
        Dispenser dispenser = Dispenser.INSTANCE;
        for (String i : s) {
            dispenser.distribute(i);
        }
        userAccount.setAmount(userAccount.getAmount().subtract(new BigDecimal(sum)));
        accountRepo.save(userAccount);
    }

    private int findLowest() {
        LinkedHashMap<String, Integer> currentStorage = new LinkedHashMap<>(storage.getCurrency());
        int lowest = 1; // если купюр нет, это не позволит работать другим функциям
        for (Map.Entry<String, Integer> entry : currentStorage.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (value != 0) {
                lowest = Integer.parseInt(key);
                break;
            }
        }
        return lowest;
    }

}
