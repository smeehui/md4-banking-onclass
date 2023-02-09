package com.cg.model;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "withdraws")
public class Withdraw extends BaseEntity implements Validator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @Column(name = "transaction_amount", precision = 10, scale = 0, nullable = false)
    @NotNull(message = "Withdraw amount is empty")
    private BigDecimal transactionAmount;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;
    @Column(nullable = false)
    private Boolean deleted = false;


    public Withdraw() {
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Withdraw(Long id, Customer customer, BigDecimal transactionAmount, Date createdAt, String createdBy, Boolean deleted) {
        this.id = id;
        this.customer = customer;
        this.transactionAmount = transactionAmount;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customerId) {
        this.customer = customerId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Withdraw.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Withdraw withdraw = (Withdraw) target;

        BigDecimal transactionAmount = withdraw.getTransactionAmount();
        if (String.valueOf(withdraw.getTransactionAmount()).length() < 2) {
            errors.rejectValue("transactionAmount","withdraw.minAmount");
        }else  if (transactionAmount.compareTo(BigDecimal.valueOf(10)) < 0 || transactionAmount.compareTo(BigDecimal.valueOf(500000)) > 0) {
            errors.rejectValue("transactionAmount","withdraw.amountRange");
        }

    }
}
