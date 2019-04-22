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

//
// Данный контроллер обрабатывает сам процесс выдачи денег пользователю
//

@Controller
public class DispenseController {
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private AccountRepo accountRepo;

    private Storage storage = Storage.INSTANCE;
    private Account userAccount;

    // При перенаправлении на данную страницу пользователь узнает результат операции
    @GetMapping("/dispense")
    public String post(@RequestParam(name = "sum", required = true) String strSum, Map<String, Object> model, Principal principal) {

        // Проверка на корректность вводимого значения
        int sum;
        try {
            sum = Integer.parseInt(strSum);
        } catch (Exception e) {
            model.put("message", "Необходимо ввести число"); // Сообщение об ошибке
            return "error_user";
        }
        Card card = cardRepo.findByNumber(principal.getName()); // Получение информации из бд о карточке пользователя
        userAccount = accountRepo.findById(card.getAccount_id()); // Получение информации из бд об аккаунте пользователя

        BigDecimal userMoney = new BigDecimal(userAccount.getAmount().toString()); // Запись текущего кол-ва денег пользователя

        // Проверка запрашиваемой суммы и её обработка
        if (chkSum(sum, userMoney) == 0 || procedure(sum) == 0) {
            model.put("message", "Автомат не может выдать данную сумму"); // Сообщение об ошибке
            return "error_user";
        }

        // Пустой вызов принтера для имитации печати чека
        Printer printer = Printer.INSTANCE;
        printer.print();
        model.put("print", "Печать чека");

        model.put("dispense", "Деньги выданы"); // Вывод положительного результата выполнения операции
        return "dispense";
    }

    // Проверка суммы
    private int chkSum(int sum, BigDecimal uM) {
        if (sum > 40000) {
            // Сумма должна быть меньше 40000
            return 0;
        }
        if (uM.compareTo(new BigDecimal(sum)) < 0) {
            // Сумма больше, чем денег на счёте
            return 0;
        }
        if (sum % findLowest() != 0) {
            // Сумма обязана делиться без остатка на наименьшую имеющуюся купюру
            return 0;
        }
        if (sum == 0) {
            // Нельзя выдавать 0
            return 0;
        }
        return 1;
    }

    // Обработка суммы
    private int procedure(int sum) {

        int currentSum = sum; // Исходное значение понадобться даллее, поэтому оно скопировано в currentSum
        LinkedHashMap<String, Integer> currentStorage = new LinkedHashMap<>(storage.getCurrency()); // Получение карты купюр в банкомате
        ArrayList<String> chain = new ArrayList<>(); // Список содержащий последовательность выдаваемых купюр
        ArrayList<String> keyList = new ArrayList<>(currentStorage.keySet()); // Список всех номиналов купюр

        // Далее реализуется жадный алгоритм подбора необходимой суммы исходя из имеющихся номиналов банкнот
        for (int i = keyList.size() - 1; i >= 0 && currentSum > 0; i--) {
            int a = Integer.parseInt(keyList.get(i)); // Получение числового представления текущего номинала
            // Пока суммма не станет равна 0, алгоритм будет отнимать от неё купюру наибольшего подходящего и доступного номинала
            if (currentSum >= a && currentStorage.get(keyList.get(i)) > 0) {
                currentSum -= a;
                currentStorage.replace(keyList.get(i), currentStorage.get(keyList.get(i)), currentStorage.get(keyList.get(i)) - 1);
                chain.add(keyList.get(i));
                i++;
            }
        }
        // Проверка результата работы алгоритма (сумма стала = 0, а кол-во купюр не превышает 40)
        if (currentSum == 0 && chain.size() <= 40) {
            throwCash(chain, sum);
        } else {
            // Автомат не может выдать сумму
            return 0;
        }

        return 1;
    }

    // Данный метод вызывает имитацию выдачи денег пользователю
    private void throwCash(ArrayList<String> s, int sum) {
        Dispenser dispenser = Dispenser.INSTANCE;
        for (String i : s) {
            dispenser.distribute(i);
        }
        userAccount.setAmount(userAccount.getAmount().subtract(new BigDecimal(sum))); // Изменение счёта клиента
        accountRepo.save(userAccount); // Сохранение изменений
    }

    // Метод позволяющий найти наименьшую доступную купюру
    private int findLowest() {
        LinkedHashMap<String, Integer> currentStorage = new LinkedHashMap<>(storage.getCurrency());
        int lowest = 1; // Если купюр нет, это не позволит работать другим функциям
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
