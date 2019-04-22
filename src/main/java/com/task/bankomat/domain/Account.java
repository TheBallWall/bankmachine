package com.task.bankomat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int id;
    private int client_id;
    private String number;
    private BigDecimal amount;
    private int status_id;

    public int getStatus_id() {
        return status_id;
    }

    public int getId() {
        return id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getClient_id() {
        return client_id;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
