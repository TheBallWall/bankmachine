package com.task.bankomat.controller;

import com.task.bankomat.repos.ClientRepo;
import com.task.bankomat.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

//
// Данный контроллер показывает приветсвтенную страницу и главное меню пользователя
//

@Controller
public class MainController {

    // При первом посещении пользователь увидет страницу приветствия
    @GetMapping("/")
    public String local() {

        return "local";
    }

    // При переходе на эту страницу пользователь увидет главное меню
    @GetMapping("/main_page")
    public String main_page() {

        return "main_page";
    }

}
