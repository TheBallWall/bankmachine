package com.task.bankomat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int clientId;
    private String firstName;
    private String lastName;

    public int getId() {
        return id;
    }

    public int getClientId() {
        return clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastname() {
        return lastName;
    }
}
