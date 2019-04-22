package com.task.bankomat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ReplenishController {
    @GetMapping("/replenish")
    public String trans(Map<String, Object> model) {

        return "replenish";
    }
}

