package com.task.bankomat.phisical;

public class Printer {
    public static final Printer INSTANCE = new Printer();

    private Printer() { }

    public void print(){
        // имитация печати чека
    }
}
