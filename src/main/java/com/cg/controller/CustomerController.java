package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.model.dto.DepositRequestDTO;
import com.cg.model.dto.TransferRequestDTO;
import com.cg.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    ICustomerService customerService;

    @GetMapping
    public String showAllCustomers(Model model) {

        model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse());

        return "customer/list";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {

        model.addAttribute("customer", new Customer());

        return "customer/create";
    }

    @GetMapping("/update/{id}")
    public String showUpdatePage(@PathVariable Long id, Model model) {

        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
        } else {
            Customer customer = customerOptional.get();
            model.addAttribute("customer", customer);
        }

        return "customer/update";
    }

    @GetMapping("/data")
    public String showCustomerData(@RequestParam Optional<String> search, Model model, Pageable pageable) {
        if (search.isPresent()) {
            String q = search.get();
            model.addAttribute("customers", customerService.search(pageable, q));

        } else model.addAttribute("customers", customerService.findCustomerByDeletedIsFalse(pageable));

        return "customer/data";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {

        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {

            model.addAttribute("error", true);
        } else {

            Customer customer = customerOptional.get();
            customer.setDeleted(true);
            customerService.save(customer);
            model.addAttribute("success", "DepositRequestDTO " + customer.getFullName() + " is deleted successfully");
        }

        return "redirect:/customers";
    }


    @GetMapping("/deposit/{id}")
    public String showCreateDepositsForm(@PathVariable Long id, Model model) {

        DepositRequestDTO depositDTO = new DepositRequestDTO();
        model.addAttribute("depositRequestDTO", depositDTO);

        Optional<Customer> customerOptional = customerService.findById(id);

        if (customerOptional.isPresent()) {
            depositDTO.setCustomer(customerOptional.get());
        } else {
            model.addAttribute("error", true);
        }
        return "deposit/create";
    }

    @GetMapping("/withdraw/{id}")
    public String showCreateWithdrawForm(@PathVariable Long id, Model model) {

        Withdraw withdraw = new Withdraw();
        model.addAttribute("withdraw", withdraw);

        Optional<Customer> customerOptional = customerService.findById(id);

        if (customerOptional.isPresent()) {
            withdraw.setCustomer(customerOptional.get());
        } else {
            model.addAttribute("error", true);
        }
        return "withdraw/create";
    }

    @GetMapping("/transfer/{senderId}")
    public String showTransferPage(@PathVariable Long senderId, Model model) {

        Optional<Customer> senderOptional = customerService.findById(senderId);

        if (!senderOptional.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("messages", "Sender not found");
        }
        else {
            Customer sender = senderOptional.get();

            TransferRequestDTO transferDTO = new TransferRequestDTO();
            transferDTO.setSender(sender);

            model.addAttribute("transferDTO", transferDTO);

            List<Customer> recipients = customerService.findAllByIdNotAndDeletedIsFalse(senderId);

            model.addAttribute("recipients", recipients);
        }

        return "transfer/create";
    }


    @PostMapping("/create")
    public String doCreate(@Valid @ModelAttribute Customer customer, BindingResult bindingResult, Model model) {
//        new DepositRequestDTO().validate(customer, bindingResult);
//        if (!bindingResult.hasErrors()) {
//            customerService.save(customer);
//            model.addAttribute("success", "New customer created successfully!");
//        }
        return "customer/create";
    }

    @PostMapping("/update/{id}")
    public String doUpdate(@PathVariable Long id, @ModelAttribute Customer customer, Model model) {

        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
        } else {
            customer.setId(id);
            customer.setBalance(customerOptional.get().getBalance());
            customerService.save(customer);

            model.addAttribute("success", "DepositRequestDTO updated successfully!");
            model.addAttribute("customer", customer);
        }

        return "customer/update";
    }

    @PostMapping("/deposit/{customerId}")
    public String doDeposit(@PathVariable Long customerId, @ModelAttribute("depositRequestDTO") DepositRequestDTO depositDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("depositRequestDTO", depositDTO);
        Optional<Customer> customerOpt = customerService.findById(customerId);

        new DepositRequestDTO().validate(depositDTO, bindingResult);
        if (!customerOpt.isPresent()) {
            model.addAttribute("error", true);
            return "deposit/create";
        }
        Customer customer = customerOpt.get();
        if (bindingResult.hasErrors()) {
            depositDTO.setCustomer(customer);
            return "deposit/create";
        }
        BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(depositDTO.getTransactionAmount()));

        Deposit deposit = new Deposit();
        deposit.setCustomer(customer);
        deposit.setTransactionAmount(transactionAmount);

        customerService.depositToCustomerBalance(customerId, deposit);

        depositDTO.setTransactionAmount("");
        depositDTO.setCustomer(deposit.getCustomer());
        model.addAttribute("success", "Deposited to customer " + deposit.getCustomer().getFullName() + " account successfully");

//        if (!bindingResult.hasErrors()) {
//            new Deposit().validate(deposit, bindingResult);
//            if (!bindingResult.hasErrors()) {
//                deposit.setCustomer(customer);
//                customerService.depositToCustomerBalance(customerId, deposit);
//
//                BigDecimal newBalance = customer.getBalance().add(deposit.getTransactionAmount());
//                customer.setBalance(newBalance);
//                deposit.setTransactionAmount(BigDecimal.ZERO);
//
//                model.addAttribute("deposit", deposit);
//                model.addAttribute("success", "Deposited to customer " + deposit.getCustomer().getFullName() + " account successfully");
//            }
//        }

        return "deposit/create";
    }

    @PostMapping("/withdraw/{customerId}")
    public String doDeposit(@PathVariable Long customerId, @Validated @ModelAttribute Withdraw withdraw, BindingResult bindingResult, Model model) {

        Customer customer = customerService.findById(customerId).get();
        withdraw.setCustomer(customer);

        if (!bindingResult.hasErrors()) {

            new Withdraw().validate(withdraw, bindingResult);
            if (!bindingResult.hasErrors()) {

                if (customer.getBalance().compareTo(withdraw.getTransactionAmount()) < 0) {

                    model.addAttribute("error", "DepositRequestDTO's balance is not enough");
                } else {

                    customerService.withdrawFromCustomerBalance(customerId, withdraw);

                    BigDecimal newBalance = customer.getBalance().subtract(withdraw.getTransactionAmount());
                    customer.setBalance(newBalance);
                    withdraw.setTransactionAmount(BigDecimal.ZERO);
                    model.addAttribute("success", "Withdraw to customer " + withdraw.getCustomer().getFullName() + " account successfully");
                }
            }

        }
        model.addAttribute("withdraw", withdraw);
        return "withdraw/create";
    }
    @PostMapping("/transfer/{senderId}")
    public String doTransfer(@PathVariable Long senderId, @ModelAttribute TransferRequestDTO transferRequestDTO, BindingResult bindingResult, Model model) {

        new TransferRequestDTO().validate(transferRequestDTO, bindingResult);

        Optional<Customer> senderOptional = customerService.findById(senderId);
        List<Customer> recipients = customerService.findAllByIdNotAndDeletedIsFalse(senderId);

        model.addAttribute("recipients", recipients);
        model.addAttribute("transferDTO", transferRequestDTO);
        if (!senderOptional.isPresent()) {
            model.addAttribute("messages", "Sender not valid");

            return "transfer/create";
        }
        transferRequestDTO.setSender(senderOptional.get());

        if (bindingResult.hasFieldErrors()) {

            return "transfer/create";
        }



        Long recipientId = transferRequestDTO.getRecipient().getId();

        Optional<Customer> recipientOptional = customerService.findById(recipientId);

        if (!recipientOptional.isPresent()) {
            model.addAttribute("messages", "Recipient not valid");

            return "transfer/create";
        }

        if (senderId.equals(recipientId)) {
            model.addAttribute("messages", "Sender ID must be different from Recipient ID");

            return "transfer/create";
        }

        BigDecimal senderCurrentBalance = senderOptional.get().getBalance();

        String transferAmountStr = transferRequestDTO.getTransferAmount();

        BigDecimal transferAmount = BigDecimal.valueOf(Long.parseLong(transferAmountStr));
        long fees = Long.parseLong(transferRequestDTO.getFees());
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));
        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        if (senderCurrentBalance.compareTo(transactionAmount) < 0) {
            model.addAttribute("messages", "Sender balance not enough to transfer");

            return "transfer/create";
        }

        Transfer transfer = new Transfer();
        transfer.setSender(senderOptional.get());
        transfer.setRecipient(recipientOptional.get());
        transfer.setTransferAmount(transferAmount);
        transfer.setFees(fees);
        transfer.setFeesAmount(feesAmount);
        transfer.setTransactionAmount(transactionAmount);

        customerService.transfer(transfer);

        transferRequestDTO.setSender(transfer.getSender());
        transferRequestDTO.setTransferAmount(null);
        transferRequestDTO.setTransactionAmount(null);

        model.addAttribute("transferDTO", transferRequestDTO);

        model.addAttribute("success", "Transfer success");

        return "transfer/create";
    }
}
