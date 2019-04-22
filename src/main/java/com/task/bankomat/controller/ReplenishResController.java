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

//
// Данный контроллер обрабатывает внесение средств на счёт
//

@Controller
public class ReplenishResController {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Storage storage = Storage.INSTANCE;
    private Account userAccount;

    // При перенаправлении на данную страницу пользователь увидет результат выполнения операции
    @GetMapping("/replenish_res")
    public String replenish(@RequestParam(name = "num", required = true) String[] strSum, Map<String, Object> model, Principal principal) {
        // Проверка на заполнение всех полей на странице имитирующей пвнесение средств (replenish_res.mustache)
        if (Arrays.asList(strSum).contains("")) {
            // Указаны не все купюры
            model.put("message", "Пожалуйста, заполните все поля");
            return "error_user";
        }

        LinkedHashMap<String, Integer> currentLoad = new LinkedHashMap<>(storage.getCurrency()); // Получение текущего состояния хранилища купюр банкомата
        int i = 0;
        // Далее происходит замена значений в созданной карте на значения внесённых купюр, а также проверка на корректный ввод чисел
        for (Map.Entry<String, Integer> entry : currentLoad.entrySet()) {
            try {
                currentLoad.replace(entry.getKey(), Integer.parseInt(strSum[i]));
                i++;
            } catch (Exception e) {
                model.put("message", "Необходимо ввести целое число");
                return "error_user";
            }
        }
        Card card = cardRepo.findByNumber(principal.getName()); // Получение информации из бд о карточке пользователя
        userAccount = accountRepo.findById(card.getAccount_id()); // Получение информации из бд об аккаунте пользователя

        // Вызов основной процедуры внесения средств
        procedure(currentLoad);

        // Пустой вызов принтера для имитации печати чека
        Printer printer = Printer.INSTANCE;
        printer.print();
        model.put("print", "Печать чека");

        model.put("repl", "Ваш счёт пополнен"); // Вывод положительного результата выполнения операции
        return "replenish_res";
    }

    // Данный мметод формирует цепочку загружаемых купюр
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

    // Данный метод вызывает обновление хранилища купюр и обновление состояния баланса
    private void loadMoney(ArrayList<String> chain, int sum) {
        Acceptor acceptor = Acceptor.INSTANCE;
        for (String i : chain) {
            acceptor.deposit(i);
        }
        userAccount.setAmount(userAccount.getAmount().add(new BigDecimal(sum))); // Установка нового значения счёта клиента
        accountRepo.save(userAccount); // Сохраниение изменений
    }
}
