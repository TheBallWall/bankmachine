package com.task.bankomat.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int id;
    private int account_id;
    private String number;
    private String backCode;
    private String pin;
    private Date expiringDate;

    public int getId() {
        return id;
    }

    public int getAccount_id() {
        return account_id;
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
