package com.task.bankomat.repos;

import com.task.bankomat.domain.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepo extends CrudRepository<Person, Long> {
}
