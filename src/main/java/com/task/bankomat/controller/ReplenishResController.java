package com.task.bankomat.controller;

import com.task.bankomat.Response;
import com.task.bankomat.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

//
// Данный контроллер обрабатывает внесение средств на счёт
//

@Controller
public class ReplenishResController {

    private final OperationService operationService; // Сервис для выполнения денежных операций пользователя

    @Autowired
    public ReplenishResController(OperationService operationService) {
        this.operationService = operationService;
    }

    // При перенаправлении на данную страницу пользователь увидет результат выполнения операции
    @GetMapping("/replenish_res")
    public String replenish(@RequestParam(name = "num", required = true) String[] strSum, Map<String, Object> model, Principal principal) {
        Response response = operationService.replenishOperation(strSum, principal.getName(), model);
        model = response.getModel();

        return response.getReturnValue();
    }
}
