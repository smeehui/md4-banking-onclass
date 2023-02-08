package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICustomerService extends IGeneralService<Customer> {
    public List<Customer> findCustomerByDeletedIsFalse();
    public Page<Customer> findCustomerByDeletedIsFalse(Pageable pageable);

    public Page<Customer> search(Pageable pageable, String keyword);

}
