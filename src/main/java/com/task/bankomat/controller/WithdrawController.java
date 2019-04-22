package com.task.bankomat.controller;

import com.task.bankomat.phisical.Storage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class WithdrawController {

    private Storage storage = Storage.INSTANCE;

    @GetMapping("/withdraw")
    public String withdraw ( Map<String, Object> model){
        ArrayList<String> available = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : storage.getCurrency().entrySet()) {
            if(entry.getValue() > 0 ){
                available.add(entry.getKey());
            }
        }
        model.put("banknotes",available);
        return "withdraw";
    }
}
