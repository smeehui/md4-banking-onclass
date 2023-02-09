package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.dto.DepositRequestDTO;
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
        model.addAttribute("depositRequestDTO", new DepositRequestDTO());

        return "deposit/create_manual";
    }
    @PostMapping("/create")
    public String doCreate(@Validated @ModelAttribute DepositRequestDTO depositRequestDTO, BindingResult bindingResult, Model model) {

        Optional<Customer> customerOpt = customerService.findById(depositRequestDTO.getCustomer().getId());
        if (customerOpt.isPresent()) {
            new DepositRequestDTO().validate(depositRequestDTO,bindingResult);
            if (!bindingResult.hasErrors()) {
                Deposit deposit = new Deposit();
                Customer customer = customerOpt.get();
                deposit.setCustomer(customer);
                long transcationAmount = Long.parseLong(depositRequestDTO.getTransactionAmount());
                deposit.setTransactionAmount(BigDecimal.valueOf(transcationAmount));

                deposit = customerService.depositToCustomerBalance(customer.getId(), deposit);
                customer.setBalance(customer.getBalance().add(deposit.getTransactionAmount()));
                deposit.setTransactionAmount(BigDecimal.ZERO);

                model.addAttribute("success", "Deposited to customer " + deposit.getCustomer().getFullName() + " account successfully");
            }

        } else {
           model.addAttribute("error", true);
       }
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());
        model.addAttribute("depositRequestDTO", depositRequestDTO);
        return "deposit/create_manual";
    }
}
