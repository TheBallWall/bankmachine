package com.task.bankomat.phisical;

import java.io.IOException;

//
// Данный класс представляет собой физический объект: купюроприёмник
//

public class Acceptor {
    public static final Acceptor INSTANCE = new Acceptor();

    private Acceptor() {
    }

    // Данный метод вызывается при внесении банкнот
    public void deposit(String val) {
        updateStorage(val);
        chkMoney();
    }

    private int chkMoney() {
        // Имитация проверки купюры
        return 1;
    }

    // Данный метод обновляет значение хранимых купюр для конкретного номинала
    private void updateStorage(String val) {
        Storage storage = Storage.INSTANCE;
        try {
            storage.update(val, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
