package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        }
        else {
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

        }else  model.addAttribute("customers",customerService.findCustomerByDeletedIsFalse(pageable));

        return "customer/data";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {

        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {

            model.addAttribute("error", true);
        }
        else {

            Customer customer = customerOptional.get();
            customer.setDeleted(true);
            customerService.save(customer);
            model.addAttribute("success", "Customer " + customer.getFullName() + " is deleted successfully");
        }

        return "redirect:/customers";
    }

    @PostMapping("/create")
    public String doCreate(@ModelAttribute Customer customer, Model model) {

        customerService.save(customer);
        model.addAttribute("success", "New customer created successfully!");
        return "customer/create";
    }
    @GetMapping("/deposit/{id}")
    public String showCreateDepositsForm(@PathVariable Long id, Model model) {

        model.addAttribute("deposit", new Deposit());



        return "deposit/create";
    }

    @PostMapping("/update/{id}")
    public String doUpdate(@PathVariable Long id, @ModelAttribute Customer customer, Model model) {

        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
        }
        else {
            customer.setId(id);
            customer.setBalance(customerOptional.get().getBalance());
            customerService.save(customer);

            model.addAttribute("success", "Customer updated successfully!");
            model.addAttribute("customer", customer);
        }

        return "customer/update";
    }
}
