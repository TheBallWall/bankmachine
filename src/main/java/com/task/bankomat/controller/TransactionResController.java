package com.task.bankomat.controller;

import com.task.bankomat.Response;
import com.task.bankomat.domain.Account;
import com.task.bankomat.domain.Card;
import com.task.bankomat.physical.Printer;
import com.task.bankomat.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

//
// Данный контроллер обрабатывает операцию перевода средств между пользователями
//

@Controller
public class TransactionResController {

    private final OperationService operationService; // Сервис для выполнения денежных операций пользователя

    @Autowired
    public TransactionResController(OperationService operationService) {
        this.operationService = operationService;
    }

    // При перенаправлении на данную страницу пользователь узнает результат выполнения операции
    @GetMapping("/transaction_res")
    public String trans(@RequestParam(name = "num", required = true) String strNum, @RequestParam(name = "sum", required = true) String strSum, Map<String, Object> model, Principal principal) {
        Response response = operationService.transactionOperation(strNum, strSum, principal.getName(), model);
        model = response.getModel();

        return response.getReturnValue();
    }
}