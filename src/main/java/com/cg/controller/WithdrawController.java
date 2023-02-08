package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Withdraw;
import com.cg.service.customer.ICustomerService;
import com.cg.service.withdraw.IWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/withdraws")
public class WithdrawController {

    @Autowired
    IWithdrawService withdrawService;

    @Autowired
    ICustomerService customerService;

    @GetMapping
    public String showAllWithdraws(Model model) {

        model.addAttribute("withdraws", withdrawService.findAll());

        return "withdraw/list";
    }

    @GetMapping("/create")
    public String createWithdraws(Model model) {

        model.addAttribute("withdraw", new Withdraw());
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());

        return "withdraw/create_manual";
    }

    @PostMapping("/create")
    public String doCreate(@ModelAttribute Withdraw withdraw, Model model) {

        withdraw.setCreatedAt(new Date());
        withdraw.setCreatedBy("admin");
        Optional<Customer> customerOpt = customerService.findById(withdraw.getCustomer().getId());
        if (customerOpt.isPresent()) {

            Customer customer = customerOpt.get();
            customerService.withdrawFromCustomerBalance(customer.getId(), withdraw);
            customer.setBalance(customer.getBalance().add(withdraw.getTransactionAmount()));
            withdraw.setTransactionAmount(BigDecimal.ZERO);
        } else {
            model.addAttribute("error", true);
        }
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());
        return "withdraw/create_manual";
    }
}
