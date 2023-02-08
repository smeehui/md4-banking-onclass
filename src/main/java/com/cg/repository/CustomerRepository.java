package com.cg.repository;

import com.cg.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public List<Customer> findCustomerByDeletedIsFalse();
    public Page<Customer> findCustomerByDeletedIsFalse(Pageable pageable);
    @Query("SELECT c FROM Customer c WHERE CONCAT(c.fullName, ' ', c.address, ' ', c.balance, ' ', c.email, ' ', c.phone) LIKE %?1%")
    public Page<Customer> search(Pageable pageable,String keyword);
}
