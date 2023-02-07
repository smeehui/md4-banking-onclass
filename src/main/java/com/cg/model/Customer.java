package com.cg.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    private String email;
    private String phone;

    @Column(precision = 10, scale = 0)
    private BigDecimal amount;
    private String address;

    @Column(nullable = false,columnDefinition = "boolean default false")
    private Boolean deleted;

    public Customer(Long id, String fullName, String email, String phone, BigDecimal amount, String address, Boolean deleted) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.amount = amount;
        this.address = address;
        this.deleted = deleted;
    }

    public Customer() {
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
