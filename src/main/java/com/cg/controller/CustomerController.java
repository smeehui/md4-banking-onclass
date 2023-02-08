package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Withdraw;
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
            model.addAttribute("success", "Customer " + customer.getFullName() + " is deleted successfully");
        }

        return "redirect:/customers";
    }


    @GetMapping("/deposit/{id}")
    public String showCreateDepositsForm(@PathVariable Long id, Model model) {

        Deposit deposit = new Deposit();
        model.addAttribute("deposit", deposit);

        Optional<Customer> customerOptional = customerService.findById(id);

        if (customerOptional.isPresent()) {
            deposit.setCustomer(customerOptional.get());
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

    @PostMapping("/create")
    public String doCreate(@Valid @ModelAttribute Customer customer,BindingResult bindingResult, Model model) {
        new Customer().validate(customer, bindingResult);
        if (!bindingResult.hasErrors()) {
            customerService.save(customer);
            model.addAttribute("success", "New customer created successfully!");
        }
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

            model.addAttribute("success", "Customer updated successfully!");
            model.addAttribute("customer", customer);
        }

        return "customer/update";
    }

    @PostMapping("/deposit/{customerId}")
    public String doDeposit(@PathVariable Long customerId, @ModelAttribute Deposit deposit, Model model) {

        model.addAttribute("deposit", deposit);


        Customer customer = customerService.findById(customerId).get();
        deposit.setCustomer(customer);
        customerService.depositToCustomerBalance(customerId, deposit);

        BigDecimal newBalance = customer.getBalance().add(deposit.getTransactionAmount());
        customer.setBalance(newBalance);
        deposit.setTransactionAmount(BigDecimal.ZERO);

        model.addAttribute("deposit", deposit);
        model.addAttribute("success", "Deposited to customer " + deposit.getCustomer().getFullName() + " account successfully");

        return "deposit/create";
    }

    @PostMapping("/withdraw/{customerId}")
    public String doDeposit(@PathVariable Long customerId, @ModelAttribute Withdraw withdraw, Model model) {

        Customer customer = customerService.findById(customerId).get();
        withdraw.setCustomer(customer);
        if (customer.getBalance().compareTo(withdraw.getTransactionAmount()) < 0) {
            model.addAttribute("error", "Customer's balance is not enough");
        } else {

            customerService.withdrawFromCustomerBalance(customerId, withdraw);

            BigDecimal newBalance = customer.getBalance().subtract(withdraw.getTransactionAmount());
            customer.setBalance(newBalance);
            withdraw.setTransactionAmount(BigDecimal.ZERO);
            model.addAttribute("success", "Withdraw to customer " + withdraw.getCustomer().getFullName() + " account successfully");
        }
        model.addAttribute("withdraw", withdraw);
        return "withdraw/create";
    }
}
