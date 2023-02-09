package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Transfer;
import com.cg.service.customer.ICustomerService;
import com.cg.service.transfer.ITransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers")
public class TransferController {
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransferService transferService;

    @GetMapping()
    public ModelAndView showAllTransfers() {
        ModelAndView modelAndView = new ModelAndView("transfer/list");
        List<Transfer> allTransfers = transferService.findAll();
        modelAndView.addObject("transfers", allTransfers);
//        modelAndView.addObject("profit", transferService.getProfit());
        return modelAndView;
    }

    @GetMapping("/create")
    public String showTransferPage(Model model) {
        addAttr(model);
        return "transfer/create";
    }

    private void addAttr(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("transfer", new Transfer());
    }

    @PostMapping("/create")
    public String createCustomer(@Valid @ModelAttribute Transfer transfer, BindingResult bindingResult, Model model) {

        model.addAttribute("customers", customerService.findAll());
        Optional<Customer> sentCustomer = customerService.findById(transfer.getSender().getId());
        Optional<Customer> receivedCustomer = customerService.findById(transfer.getRecipient().getId());

        return "transfer/create";
    }

}
