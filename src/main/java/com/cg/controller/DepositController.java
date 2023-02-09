package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/deposits")
public class DepositController {
    @Autowired
    ICustomerService customerService;

    @Autowired
    IDepositService depositService;

    @GetMapping
    public String showDepositsPage(Model model) {
        model.addAttribute("deposits", depositService.findAll());
        return "deposit/list";
    }
    @GetMapping("/create")
    public String showCreateDepositPage(Model model) {

        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());
        model.addAttribute("deposit", new Deposit());

        return "deposit/create_manual";
    }
    @PostMapping("/create")
    public String doCreate(@Validated @ModelAttribute Deposit deposit, BindingResult bindingResult, Model model) {

        Optional<Customer> customerOpt = customerService.findById(deposit.getCustomer().getId());
        if (customerOpt.isPresent()) {

            if (!bindingResult.hasErrors()) {

                Customer customer = customerOpt.get();
                deposit = customerService.depositToCustomerBalance(customer.getId(), deposit);
                customer.setBalance(customer.getBalance().add(deposit.getTransactionAmount()));
                deposit.setTransactionAmount(BigDecimal.ZERO);

            }

        } else {
           model.addAttribute("error", true);
       }
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());
        return "deposit/create_manual";
    }
}
