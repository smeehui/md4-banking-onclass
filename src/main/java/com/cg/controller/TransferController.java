package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Transfer;
import com.cg.model.dto.TransferRequestDTO;
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

import java.math.BigDecimal;
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
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("transferRequestDTO", new TransferRequestDTO());
        return "transfer/create_manual";
    }

    @PostMapping("/create")
    public String createCustomer( @ModelAttribute TransferRequestDTO transferRequestDTO, BindingResult bindingResult, Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("transferRequestDTO", transferRequestDTO);

        new TransferRequestDTO().validate(transferRequestDTO, bindingResult);

        Customer sender = transferRequestDTO.getSender();
        Customer recipient = transferRequestDTO.getRecipient();


        if (sender == null || recipient == null) {
            model.addAttribute("error", "Sender or recipient is empty");
            return "transfer/create_manual";
        }


        Optional<Customer> senderOptional = customerService.findById(sender.getId());
        Optional<Customer> recipientOptional = customerService.findById(recipient.getId());

        if (!senderOptional.isPresent()||!recipientOptional.isPresent()) {
            model.addAttribute("error", "Sender or recipient is empty");
            return "transfer/create_manual";
        }
         sender = senderOptional.get();
         recipient = recipientOptional.get();

        transferRequestDTO.setSender(sender);
        transferRequestDTO.setRecipient(recipient);


        if (sender.getId().equals(recipient.getId())) {
            model.addAttribute("error", "Sender ID must be different from Recipient ID");
            return "transfer/create_manual";
        }

        if (bindingResult.hasFieldErrors()) {
            return "transfer/create_manual";
        }


        BigDecimal senderCurrentBalance = sender.getBalance();

        String transferAmountStr = transferRequestDTO.getTransferAmount();

        BigDecimal transferAmount = BigDecimal.valueOf(Long.parseLong(transferAmountStr));
        long fees = Long.parseLong(transferRequestDTO.getFees());
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));
        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        if (senderCurrentBalance.compareTo(transactionAmount) < 0) {
            model.addAttribute("error", "Sender balance not enough to transfer");

            return "transfer/create_manual";
        }

        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setRecipient(recipient);
        transfer.setTransferAmount(transferAmount);
        transfer.setFees(fees);
        transfer.setFeesAmount(feesAmount);
        transfer.setTransactionAmount(transactionAmount);

        customerService.transfer(transfer);

        transferRequestDTO.setSender(transfer.getSender());
        transferRequestDTO.setRecipient(transfer.getRecipient());

        transferRequestDTO.setTransferAmount(null);
        transferRequestDTO.setTransactionAmount(null);

        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("transferDTO", transferRequestDTO);

        model.addAttribute("success", "Transfer success");


        return "transfer/create_manual";
    }

}
