package com.task.bankomat.service;

import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.repos.AccountRepo;
import com.task.bankomat.repos.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

//
// Сервис для работы с пользовательской инфоромацией
//

@Service
public class UserDataService {
    private CardRepo cardRepo;
    private AccountRepo accountRepo;

    @Autowired
    public UserDataService(CardRepo cardRepo, AccountRepo accountRepo) {
        this.cardRepo = cardRepo;
        this.accountRepo = accountRepo;
    }

    // Метод возвращающий балланс пользователя в контроллер BalanceController
    @Transactional
    public BigDecimal getBalance(String name) {
        Card card = cardRepo.findByNumber(name); // Получение информации из бд о карточке пользователя
        Account account = accountRepo.findById(card.getAccountId()); // Получение информации из бд об аккаунте пользователя

        return account.getAmount();
    }


}
