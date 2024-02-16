package com.github.gustavoflor.rinha.core.service;

import com.github.gustavoflor.rinha.core.Transfer;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    public Transfer save(final Transfer transfer) {
        return transferRepository.save(transfer);
    }

    public List<Transfer> findLatest(final Integer customerId) {
        return transferRepository.findLatest(customerId);
    }

}
