package com.cg.model;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity implements Validator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name")
    @NotEmpty(message = "Full name is empty")
    private String fullName;

    @NotEmpty(message = "Email is empty")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "Phone is empty")
    @Column(unique = true)
    private String phone;

    @Column(precision = 10, columnDefinition = "DECIMAL DEFAULT 0", nullable = false, updatable = false)
    private BigDecimal balance;

    @NotEmpty(message = "Phone is empty")
    private String address;


    public Customer(Long id, String fullName, String email, String phone, BigDecimal balance, String address) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
        this.address = address;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer customer = (Customer) target;

        String fullName = customer.getFullName();
        String email = customer.getEmail();
        if (fullName.length() < 4 || fullName.length() > 25) {
            errors.rejectValue("fullName", "fullName.length");
        }

        if (!email.matches("^[\\w]+@([\\w-]+\\.)+[\\w-]{2,6}$")) {
            errors.rejectValue("email", "email.matches");
        }
    }
}
