package com.github.gustavoflor.rinha.core.usecase.impl;

import com.github.gustavoflor.rinha.core.exception.NotFoundException;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import com.github.gustavoflor.rinha.core.service.CustomerService;
import com.github.gustavoflor.rinha.core.usecase.TransferUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class TransferUseCaseImpl implements TransferUseCase {

    private final TransactionTemplate transactionTemplate;
    private final CustomerService customerService;
    private final TransferRepository transferRepository;

    @Override
    public Output execute(final Input input) {
        final var customerId = input.customerId();

        // TODO: Try acquire lock with customer ID

        return transactionTemplate.execute(status -> {
            final var customer = customerService.findById(customerId).orElseThrow(NotFoundException::new);
            final var transfer = input.transfer();

            transfer.apply(customer);

            customerService.save(customer);
            transferRepository.save(transfer);

            return new Output(customer);
        });
    }

}
