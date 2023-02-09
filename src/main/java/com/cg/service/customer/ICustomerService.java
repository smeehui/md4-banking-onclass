package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICustomerService extends IGeneralService<Customer> {
    public List<Customer> findCustomerByDeletedIsFalse();
    public Page<Customer> findCustomerByDeletedIsFalse(Pageable pageable);

    public Page<Customer> search(Pageable pageable, String keyword);

    Deposit depositToCustomerBalance(Long id, Deposit deposit);

    List<Customer> findAllByIdNotAndDeletedIsFalse(Long senderId);

    void withdrawFromCustomerBalance(Long customerId, Withdraw withdraw);

    Transfer transfer(Transfer transfer);
}
