package com.task.bankomat.phisical;

//
// Данный класс представляет собой физический объект: принтер
//

public class Printer {
    public static final Printer INSTANCE = new Printer();

    private Printer() {
    }

    // Заглушка, имитирующая печать
    public void print() {

    }
}
