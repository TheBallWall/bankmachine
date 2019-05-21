package com.task.bankomat.physical;

import java.io.IOException;

//
// Данный класс представляет собой физический объект: диспенсер
//

public class Dispenser {
    public static final Dispenser INSTANCE = new Dispenser();

    private Dispenser() {
    }

    // Данный метод вызывается при выдаче банкнот
    public void distribute(String val) {
        updateStorage(val);
    }

    // Данный метод обновляет значение хранимых купюр для конкретного номинала
    private void updateStorage(String val) {
        Storage storage = Storage.INSTANCE;
        try {
            storage.update(val, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
