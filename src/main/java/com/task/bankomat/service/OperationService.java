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
import java.util.*;

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
        this.printer = Printer.INSTANCE;
        this.acceptor = Acceptor.INSTANCE;
    }

    //
    // Код, отвечающий за выполнение операции выдачи денежных средств
    //

    @Transactional
    public Response dispenseOperation(String strSum, String name, Map<String, Object> model) {
        // Проверка на корректность вводимого значения
        int sum;
        try {
            sum = Integer.parseInt(strSum);
        } catch (Exception e) {
            model.put("message", "Необходимо ввести число"); // Сообщение об ошибке
            return new Response(model, "error_user");
        }

        Card card = cardRepo.findByNumber(name); // Получение информации из бд о карточке пользователя
        account = accountRepo.findById(card.getAccountId()); // Получение информации из бд об аккаунте пользователя

        BigDecimal userMoney = new BigDecimal(account.getAmount().toString()); // Запись текущего кол-ва денег пользователя

        // Проверка запрашиваемой суммы и её обработка
        if (chkSum(sum, userMoney) == 0 || procedure(sum) == 0) {
            model.put("message", "Автомат не может выдать данную сумму"); // Сообщение об ошибке
            return new Response(model, "error_user");
        }

        // Пустой вызов принтера для имитации печати чека
        printer.print();
        model.put("print", "Печать чека");

        model.put("dispense", "Деньги выданы"); // Вывод положительного результата выполнения операции
        return new Response(model, "dispense");
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
    // Код, отвечающий за выполнение операции внесения денежных средств
    //

    @Transactional
    public Response replenishOperation(String strSum[], String name, Map<String, Object> model) {
        // Проверка на заполнение всех полей на странице имитирующей внесение средств (replenish_res.mustache)
        if (Arrays.asList(strSum).contains("")) {
            // Указаны не все купюры
            model.put("message", "Пожалуйста, заполните все поля");
            return new Response(model, "error_user");
        }

        LinkedHashMap<String, Integer> currentLoad = new LinkedHashMap<>(storage.getCurrency()); // Получение текущего состояния хранилища купюр банкомата
        int i = 0;
        // Далее происходит замена значений в созданной карте на значения внесённых купюр, а также проверка на корректный ввод чисел
        for (Map.Entry<String, Integer> entry : currentLoad.entrySet()) {
            try {
                if (Integer.parseInt(strSum[i]) < 0) {
                    throw new ArithmeticException();
                } // Проверка числа на отрицательность
                currentLoad.replace(entry.getKey(), Integer.parseInt(strSum[i])); // Обновлённое состояние хранилища
                i++;
            } catch (Exception e) {
                model.put("message", "Необходимо ввести целое положительное число");
                return new Response(model, "error_user");
            }
        }
        Card card = cardRepo.findByNumber(name); // Получение информации из бд о карточке пользователя
        account = accountRepo.findById(card.getAccountId()); // Получение информации из бд об аккаунте пользователя

        // Вызов основной процедуры внесения средств
        procedure(currentLoad);

        // Пустой вызов принтера для имитации печати чека
        printer.print();
        model.put("print", "Печать чека");

        model.put("repl", "Ваш счёт пополнен"); // Вывод положительного результата выполнения операции
        return new Response(model, "replenish_res");
    }

    // Данный метод формирует цепочку загружаемых купюр
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
        for (String i : chain) {
            acceptor.deposit(i);
        }
        account.setAmount(account.getAmount().add(new BigDecimal(sum))); // Установка нового значения счёта клиента
        accountRepo.save(account); // Сохраниение изменений
    }


    //
    // Код, отвечающий за выполнение операции перевода денежных средств
    //

    @Transactional
    public Response transactionOperation(String strNum, String strSum, String name, Map<String, Object> model) {


        // Сперва происходит проверка суммы на соответсвие числовому значению
        BigDecimal sum;
        try {
            sum = new BigDecimal(strSum);
        } catch (Exception e) {
            model.put("message", "Необходимо ввести число (например 100 или 300.25)");
            return new Response(model, "error_user");
        }
        Account recipientAccount = accountRepo.findByNumber(strNum); // Получение аккаунта пользователя, получающего перевод

        // Проверка на существование счёта и его статуса
        if (recipientAccount == null || recipientAccount.getStatusId() != 1) {
            model.put("message", "Перевод на данный счёт невозможен");
            return new Response(model, "error_user");
        }

        Card card = cardRepo.findByNumber(name); // Получение информации из бд о карточке пользователя
        account = accountRepo.findById(card.getAccountId()); // Получение информации из бд об аккаунте пользователя

        BigDecimal userMoney = new BigDecimal(account.getAmount().toString()); // Запись текущего кол-ва денег пользователя

        // Проверка суммы
        if (userMoney.compareTo(sum) < 0) {
            // Сумма больше, чем денег на счёте
            model.put("message", "На счёте недостаточно средств");
            return new Response(model, "error_user");
        }
        // Вызов основной операции перевода
        procedure(recipientAccount, sum);

        // Пустой вызов принтера для имитации печати чека
        printer.print();
        model.put("print", "Печать чека");

        model.put("trans", "Перевод совершён"); // Вывод положительного результата выполнения операции

        return new Response(model, "transaction_res");
    }

    // Данный метод реализует передачу средств
    private void procedure(Account recipientAccount, BigDecimal sum) {
        account.setAmount(account.getAmount().subtract(sum)); // Уменьшение баланса текущего клинта
        recipientAccount.setAmount(recipientAccount.getAmount().add(sum)); // Увеличение баланса клиента-получателя
        accountRepo.save(account); // Сохранение изменений
        accountRepo.save(recipientAccount); // Сохранение изменений
    }
}
