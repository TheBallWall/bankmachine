package com.task.bankomat.repos;

import com.task.bankomat.domain.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyRepo extends CrudRepository<Company,Long> {
}
