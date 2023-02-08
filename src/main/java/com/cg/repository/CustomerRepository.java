package com.cg.repository;

import com.cg.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public List<Customer> findCustomerByDeletedIsFalse();

    public Page<Customer> findCustomerByDeletedIsFalse(Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE CONCAT(c.fullName, ' ', c.address, ' ', c.balance, ' ', c.email, ' ', c.phone) LIKE %?1%")
    public Page<Customer> search(Pageable pageable, String keyword);

    @Modifying
    @Query("UPDATE Customer c SET c.balance = c.balance + :amount WHERE c.id = :customerID")
    public void addDepositAmountToCustomer(@Param("customerID") Long customerId,@Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Customer c SET c.balance = c.balance - :amount WHERE c.id = :customerID")
    public void withdrawFromCustomerBalance(@Param("customerID") Long customerId,@Param("amount") BigDecimal amount);
}
