package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.repository.CustomerRepository;
import com.cg.repository.DepositRepository;
import com.cg.repository.TransferRepository;
import com.cg.repository.WithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements ICustomerService{
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DepositRepository depositRepository;

    @Autowired
    WithdrawRepository withdrawRepository;

    @Autowired
    TransferRepository transferRepository;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Customer> findCustomerByDeletedIsFalse() {
        return customerRepository.findCustomerByDeletedIsFalse();
    }

    @Override
    public Page<Customer> findCustomerByDeletedIsFalse(Pageable pageable) {
        return customerRepository.findCustomerByDeletedIsFalse(pageable);
    }

    @Override
    public Page<Customer> search(Pageable pageable, String keyword) {
        return customerRepository.search(pageable, keyword);
    }

    @Override
    public Deposit depositToCustomerBalance(Long id, Deposit deposit) {

        deposit = depositRepository.save(deposit);

        BigDecimal oldBalance = deposit.getCustomer().getBalance();
        BigDecimal transactionAmount = deposit.getTransactionAmount();

        customerRepository.addDepositAmountToCustomer(id, transactionAmount);

        deposit.getCustomer().setBalance(oldBalance.add(transactionAmount));

        return deposit;
    }

    @Override
    public List<Customer> findAllByIdNotAndDeletedIsFalse(Long senderId) {
        return customerRepository.findAllByIdNotAndDeletedIsFalse(senderId);
    }

    @Override
    public Transfer transfer(Transfer transfer) {
        BigDecimal transferAmount = transfer.getTransferAmount();
        BigDecimal transactionAmount = transfer.getTransactionAmount();

        customerRepository.withdrawFromCustomerBalance(transfer.getSender().getId(), transactionAmount);

        customerRepository.addDepositAmountToCustomer(transfer.getRecipient().getId(), transferAmount);

        transferRepository.save(transfer);

        transfer.getSender().setBalance(transfer.getSender().getBalance().subtract(transactionAmount));
        transfer.getRecipient().setBalance(transfer.getRecipient().getBalance().add(transferAmount));

        return transfer;
    }

    @Override
    public void withdrawFromCustomerBalance(Long customerId, Withdraw withdraw) {

        withdraw = withdrawRepository.save(withdraw);

        customerRepository.withdrawFromCustomerBalance(customerId, withdraw.getTransactionAmount());

        withdraw.getCustomer().setBalance(withdraw.getCustomer().getBalance().subtract(withdraw.getTransactionAmount()));
    }
}
