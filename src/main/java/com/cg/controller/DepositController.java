package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
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
    @PostMapping("/create")
    public String doCreate(@ModelAttribute Deposit deposit, Model model) {

        deposit.setCreatedAt(new Date());
        deposit.setCreatedBy("admin");

        Optional<Customer> customerOpt = customerService.findById(deposit.getCustomer().getId());
       if (customerOpt.isPresent()) {
           Customer customer = customerOpt.get();
           customer.setBalance(customer.getBalance().add(deposit.getTransactionAmount()));
           model.addAttribute("success", "New deposit created successfully!");
           customerService.save(customer);
           depositService.save(deposit);
       }else {
           model.addAttribute("error", true);
       }
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());
        return "deposit/create";
    }
}
