package com.task.bankomat.phisical;

import java.io.IOException;

public class Dispenser {
    public static final Dispenser INSTANCE = new Dispenser();

    private Dispenser() { }

    public void distribute(String val){
        updateStorage(val);
        // имитация выдачи банкнот
    }
    private void updateStorage(String val){
        Storage storage = Storage.INSTANCE;
        try {
            storage.update(val, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
