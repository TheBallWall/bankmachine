package com.task.bankomat.controller;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.phisical.Printer;
import com.task.bankomat.repos.AccountRepo;
import com.task.bankomat.repos.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

//
// Данный контроллер обрабатывает операцию перевода средств между пользователями
//

@Controller
public class TrnsctionResController {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Account userAccount;

    // При перенаправлении на данную страницу пользователь узнает результат выполнения операции
    @GetMapping("/transaction_res")
    public String trans(@RequestParam(name = "num", required = true) String strNum, @RequestParam(name = "sum", required = true) String strSum, Map<String, Object> model, Principal principal) {
        // Сперва происходит проверка суммы на соответсвие числовому значению
        BigDecimal sum;
        try {
            sum = new BigDecimal(strSum);
        } catch (Exception e) {
            model.put("message", "Необходимо ввести число (например 100 или 300.25)");
            return "error_user";
        }
        Account recipientAccount = accountRepo.findByNumber(strNum); // Получение аккаунта пользователя, получающего перевод

        // Проверка на существование счёта и его статуса
        if (recipientAccount == null || recipientAccount.getStatus_id() != 1) {
            model.put("message", "Перевод на данный счёт невозможен");
            return "error_user";
        }

        Card card = cardRepo.findByNumber(principal.getName()); // Получение информации из бд о карточке пользователя
        userAccount = accountRepo.findById(card.getAccount_id()); // Получение информации из бд об аккаунте пользователя

        BigDecimal userMoney = new BigDecimal(userAccount.getAmount().toString()); // Запись текущего кол-ва денег пользователя

        // Проверка суммы
        if (userMoney.compareTo(sum) < 0) {
            // Сумма больше, чем денег на счёте
            model.put("message", "На счёте недостаточно средств");
            return "error_user";
        }
        // Вызов основной операции перевода
        procedure(recipientAccount, sum);

        // Пустой вызов принтера для имитации печати чека
        Printer printer = Printer.INSTANCE;
        printer.print();
        model.put("print", "Печать чека");

        model.put("trans", "Перевод совершён"); // Вывод положительного результата выполнения операции

        return "transaction_res";
    }

    // Данный метод реализует передачу средств
    private void procedure(Account rA, BigDecimal sum) {
        userAccount.setAmount(userAccount.getAmount().subtract(sum)); // Уменьшение баланса текущего клинта
        rA.setAmount(rA.getAmount().add(sum)); // Увеличение баланса клиента-получателя
        accountRepo.save(userAccount); // Сохранение изменений
        accountRepo.save(rA); // Сохранение изменений
    }
}