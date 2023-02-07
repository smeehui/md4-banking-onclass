package com.cg.service.transfer;

import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.repository.TransferRepository;
import com.cg.service.deposit.IDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransferServiceImpl implements ITransferService {
    @Autowired
    TransferRepository transferRepository;

    @Override
    public List<Transfer> findAll() {
        return null;
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Transfer save(Transfer transfer) {
        return null;
    }

    @Override
    public void delete(Transfer transfer) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
