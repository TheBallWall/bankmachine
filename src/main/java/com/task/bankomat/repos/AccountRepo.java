package com.task.bankomat.repos;

import com.task.bankomat.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepo extends CrudRepository<Account,Long> {
    Account findById(int id);
    Account findByNumber(String number);
}
