package com.task.bankomat.repos;

import com.task.bankomat.domain.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepo extends CrudRepository<Client, Long> {
}
