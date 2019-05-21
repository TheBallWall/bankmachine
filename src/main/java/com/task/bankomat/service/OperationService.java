package com.task.bankomat.service;

import com.task.bankomat.Response;
import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.physical.Acceptor;
import com.task.bankomat.physical.Dispenser;
import com.task.bankomat.physical.Printer;
import com.task.bankomat.physical.Storage;
import com.task.bankomat.repos.AccountRepo;
import com.task.bankomat.repos.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

//
// Сервис для выполнения денежных операций пользователя (выдача средств, пополнение счёта, перевод средств)
//

@Service
public class OperationService {
    private CardRepo cardRepo;
    private AccountRepo accountRepo;
    private Storage storage;
    private Dispenser dispenser;
    private Printer printer;
    private Acceptor acceptor;
    private Account account;

    @Autowired
    public OperationService(CardRepo cardRepo, AccountRepo accountRepo) {
        this.cardRepo = cardRepo;
        this.accountRepo = accountRepo;
        this.storage = Storage.INSTANCE;
        this.dispenser = Dispenser.INSTANCE;
        this.printer=Printer.INSTANCE;
        this.acceptor=Acceptor.INSTANCE;
    }

    //
    // Код, отвечающий за выполнение операции выдачи купюр
    //

    @Transactional
    public Response dispenseOperation(String strSum, String name, Map<String, Object> model){
        // Проверка на корректность вводимого значения
        int sum;
        try {
            sum = Integer.parseInt(strSum);
        } catch (Exception e) {
            model.put("message", "Необходимо ввести число"); // Сообщение об ошибке
            return new Response(model,"error_user");
        }

        Card card = cardRepo.findByNumber(name); // Получение информации из бд о карточке пользователя
        account = accountRepo.findById(card.getAccountId()); // Получение информации из бд об аккаунте пользователя

        BigDecimal userMoney = new BigDecimal(account.getAmount().toString()); // Запись текущего кол-ва денег пользователя

        // Проверка запрашиваемой суммы и её обработка
        if (chkSum(sum, userMoney) == 0 || procedure(sum) == 0) {
            model.put("message", "Автомат не может выдать данную сумму"); // Сообщение об ошибке
            return new Response(model,"error_user");
        }

        // Пустой вызов принтера для имитации печати чека
        printer.print();
        model.put("print", "Печать чека");

        model.put("dispense", "Деньги выданы"); // Вывод положительного результата выполнения операции
        return new Response(model,"dispense");
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
        for (String i : s) {
            dispenser.distribute(i);
        }
        account.setAmount(account.getAmount().subtract(new BigDecimal(sum))); // Изменение счёта клиента
        accountRepo.save(account); // Сохранение изменений
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

    //
    // Код, отвечающий за выполнение операции внесения средств
    //

    @Transactional
    public Response replenishOperation(String strSum, String name, Map<String, Object> model){
        return new Response(model,"");
    }

}
