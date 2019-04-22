package com.task.bankomat.phisical;

import java.io.IOException;

public class Acceptor {
    public static final Acceptor INSTANCE = new Acceptor();

    private Acceptor() { }

    public void deposit(String val){
        updateStorage(val);
        // имитация принятия банкнот
        chkMoney();
    }
    private int chkMoney(){
        //имитация проверки купюры
        return 1;
    }
    private void updateStorage(String val){
        Storage storage = Storage.INSTANCE;
        try {
            storage.update(val, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
