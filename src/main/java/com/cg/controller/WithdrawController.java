package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Withdraw;
import com.cg.model.dto.WithdrawRequestDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.service.withdraw.IWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
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

        model.addAttribute("withdrawRequestDTO", new WithdrawRequestDTO());
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());

        return "withdraw/create_manual";
    }

    @PostMapping("/create")
    public String doCreate(@ModelAttribute WithdrawRequestDTO withdrawRequestDTO, BindingResult bindingResult, Model model) {

        Optional<Customer> customerOpt = customerService.findById(withdrawRequestDTO.getCustomer().getId());

        model.addAttribute("withdrawRequestDTO", withdrawRequestDTO);
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            new WithdrawRequestDTO().validate(withdrawRequestDTO,bindingResult);
            if (!bindingResult.hasErrors()) {
                Withdraw withdraw = new Withdraw();

                withdraw.setCustomer(customer);
                Long transactionAmount = Long.parseLong(withdrawRequestDTO.getTransactionAmount());
                withdraw.setTransactionAmount(BigDecimal.valueOf(transactionAmount));

                customerService.withdrawFromCustomerBalance(customer.getId(), withdraw);

                withdraw.setTransactionAmount(BigDecimal.ZERO);

                model.addAttribute("success", "Withdraw from customer " + withdraw.getCustomer().getFullName() + " account successfully");
            }

        } else {

            model.addAttribute("error", true);

        }
        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());
        return "withdraw/create_manual";
    }
}
