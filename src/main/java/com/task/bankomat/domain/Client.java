package com.task.bankomat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int bankId;

    public int getId() {
        return id;
    }

    public int getBankId() {
        return bankId;
    }
}
