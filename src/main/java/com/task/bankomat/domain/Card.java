package com.task.bankomat.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int id;
    private int accountId;
    private String number;
    private String backCode;
    private String pin;
    private Date expiringDate;

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getNumber() {
        return number;
    }

    public String getBackCode() {
        return backCode;
    }

    public String getPin() {
        return pin;
    }

    public Date getExpiringDate() {
        return expiringDate;
    }
}
