package com.task.bankomat.repos;

import com.task.bankomat.domain.Card;
import org.springframework.data.repository.CrudRepository;

public interface CardRepo extends CrudRepository<Card,Long> {
    Card findByNumber(String number);
}
