package com.cg.model.dto;

import com.cg.model.Customer;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

public class DepositRequestDTO implements Validator {
    private Customer customer;
    private String transactionAmount;

    public DepositRequestDTO(Customer customer, String transactionAmount) {
        this.customer = customer;
        this.transactionAmount = transactionAmount;
    }

    public DepositRequestDTO() {
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return DepositRequestDTO.class.isAssignableFrom(clazz);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Override
    public void validate(Object target, Errors errors) {
        DepositRequestDTO castedTarget = (DepositRequestDTO) target;

        String depositAmountStr = castedTarget.getTransactionAmount();

        if (depositAmountStr.length() == 0) {
            errors.rejectValue("transactionAmount","transactionAmount.empty");
        } else if (depositAmountStr.length() >= 7) {
            errors.rejectValue("transactionAmount","transactionAmount.range");
        }else {
            if (!depositAmountStr.matches("(^$|[0-9]*$)")){
                errors.rejectValue("transactionAmount", "transactionAmount.matches");
            }else {
                BigDecimal depositAmount = BigDecimal.valueOf(Long.parseLong(depositAmountStr));

                BigDecimal min = BigDecimal.valueOf(10L);
                BigDecimal max = BigDecimal.valueOf(100000L);

                if (depositAmount.compareTo(min) < 0 ||depositAmount.compareTo(max) > 0) {
                    errors.rejectValue("transactionAmount", "transactionAmount.range");
                }
            }
        }

    }
}
