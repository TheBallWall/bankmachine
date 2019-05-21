package com.task.bankomat.service;

import com.task.bankomat.physical.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Map;

//
// Сервис для работы с хранилищем купюр
//

@Service
public class StorageService {
    private Storage storage;

    @Autowired
    public StorageService() {
        this.storage = Storage.INSTANCE;
    }

    @Transactional
    public ArrayList<String> getAvailable() {
        // Последующая логика позволяет передать список доступных купюр на страницу
        ArrayList<String> available = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : storage.getCurrency().entrySet()) { // Проверяя все элементы находящиеся в хранилище, заполняется список доступных купюр
            if (entry.getValue() > 0) {
                available.add(entry.getKey());
            }
        }
        return available;
    }

}
