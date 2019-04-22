package com.task.bankomat.phisical;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;

//
// Данный класс представляет собой физический объект: хранилище купюр
//

public class Storage {
    public static final Storage INSTANCE = new Storage();

    private LinkedHashMap<String, Integer> currency;

    private Storage() {
        this.currency = getCurrencyFromFile();
    }

    public LinkedHashMap<String, Integer> getCurrency() {
        return currency;
    }

    // Данный метод обновляет состояние хранилища, изменяя количество купюр одного номинала
    public void update(String key, boolean deposit) throws IOException {
        if (deposit) {
            currency.replace(key, currency.get(key), currency.get(key) + 1);
        } else {
            currency.replace(key, currency.get(key), currency.get(key) - 1);
        }
        writeCurrencyInFile();
    }

    // Данный метод забирает количество купюр по номиналам из файла banknotes.json
    private LinkedHashMap<String, Integer> getCurrencyFromFile() {
        LinkedHashMap<String, Integer> fileCurrency = new LinkedHashMap<>(); // Карта, представляющая из себя пару номинал-количество
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("src/main/resources/banknotes.json"));
            JSONObject jsonObject = (JSONObject) obj; // Создаётся json-объект
            JSONArray keysArray = (JSONArray) jsonObject.get("Keys"); // Получение списка ключей (номиналов)
            JSONArray valuesArray = (JSONArray) jsonObject.get("Values"); // Получение списка значений (кол-ва купюр)
            Iterator<String> keysIterator = keysArray.iterator();
            Iterator<Long> valuesIterator = valuesArray.iterator();
            // Заполнение карты данными
            while (keysIterator.hasNext()) {
                fileCurrency.put(keysIterator.next(), valuesIterator.next().intValue());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fileCurrency;
    }

    // Данный метод вносит количество купюр по номиналам в файла banknotes.json
    private void writeCurrencyInFile() throws IOException {
        JSONObject obj = new JSONObject(); // Создаётся json-объект
        JSONArray keysArray = new JSONArray(); // Список для ключей
        JSONArray valuesArray = new JSONArray(); // Список для значений

        // Внесение текущих значений в списки
        for (Map.Entry<String, Integer> entry : currency.entrySet()) {
            keysArray.add(entry.getKey());
            valuesArray.add(entry.getValue());
        }

        // Внесение значений в объект
        obj.put("Keys", keysArray);
        obj.put("Values", valuesArray);

        // Запись данных в файл
        FileWriter file = new FileWriter("src/main/resources/banknotes.json");
        try {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.close();
        }
    }
}

