package com.task.bankomat.repos;

import com.task.bankomat.domain.Bank;
import org.springframework.data.repository.CrudRepository;

public interface BankRepo extends CrudRepository<Bank,Long> {
}
