package com.task.bankomat.controller;

import com.task.bankomat.Response;
import com.task.bankomat.service.OperationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

//
// Данный контроллер обрабатывает сам процесс выдачи денег пользователю
//

@Controller
public class DispenseController {

    private final OperationService operationService; // Сервис для выполнения денежных операций пользователя

    public DispenseController(OperationService operationService) {
        this.operationService = operationService;
    }

    // При перенаправлении на данную страницу пользователь узнает результат операции
    @GetMapping("/dispense")
    public String dispense(@RequestParam(name = "sum", required = true) String strSum, Map<String, Object> model, Principal principal) {
        Response response = operationService.dispenseOperation(strSum, principal.getName(), model);
        model = response.getModel();

        return response.getReturnValue();
    }
}
