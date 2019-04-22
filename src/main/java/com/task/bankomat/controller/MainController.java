package com.task.bankomat.controller;

import com.task.bankomat.domain.Client;
import com.task.bankomat.domain.Person;
import com.task.bankomat.repos.ClientRepo;
import com.task.bankomat.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private ClientRepo clientRepo;
    @Autowired
    private PersonRepo personRepo;

    @GetMapping("/")
    public String local(String name, Map<String, Object> model){

        return "local";
    }

    @GetMapping("/main_page")
    public String main_page(Map<String, Object> model){

        return "main_page";
    }

}
