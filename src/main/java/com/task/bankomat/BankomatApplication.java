package com.task.bankomat;

import com.task.bankomat.phisical.Dispenser;
import com.task.bankomat.phisical.Storage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankomatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankomatApplication.class, args);
    }

}
