package com.example.bankingserviceone;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankingServiceController {
    @RequestMapping(value = "/withdrawal/{amount}/{account}", method = RequestMethod.GET)
    public String withdrawl(@PathVariable("amount") int amount, @PathVariable("account") String account) {
        return "Withdrawing " + amount + " from " + account;
    }
}
