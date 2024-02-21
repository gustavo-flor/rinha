package com.github.gustavoflor.rinha.core.usecase.transfer.impl;

import com.github.gustavoflor.rinha.core.exception.NotFoundException;
import com.github.gustavoflor.rinha.core.exception.LockException;
import com.github.gustavoflor.rinha.core.service.CustomerService;
import com.github.gustavoflor.rinha.core.service.LockService;
import com.github.gustavoflor.rinha.core.service.TransferService;
import com.github.gustavoflor.rinha.core.usecase.transfer.TransferUseCase;
import com.github.gustavoflor.rinha.core.usecase.transfer.TransferUseCaseInput;
import com.github.gustavoflor.rinha.core.usecase.transfer.TransferUseCaseOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static com.github.gustavoflor.rinha.core.config.CacheConfig.GET_STATEMENT_KEY;
import static java.text.MessageFormat.format;

@Component
@RequiredArgsConstructor
public class TransferUseCaseImpl implements TransferUseCase {

    private static final String TRANSFER_LOCK_KEY_TEMPLATE = "do-transfer.{0}";
    private static final Duration TRY_LOCK_DURATION = Duration.ofSeconds(60);

    private final TransactionTemplate transactionTemplate;
    private final CustomerService customerService;
    private final TransferService transferService;
    private final LockService lockService;

    @Override
    @CacheEvict(cacheNames = GET_STATEMENT_KEY, key = "#input.customerId()", beforeInvocation = true)
    public TransferUseCaseOutput execute(final TransferUseCaseInput input) {
        final var lockKey = format(TRANSFER_LOCK_KEY_TEMPLATE, input.customerId());
        try {
            return lockService.tryLock(lockKey, TRY_LOCK_DURATION, () -> transfer(input));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

    private TransferUseCaseOutput transfer(final TransferUseCaseInput input) {
        return transactionTemplate.execute(status -> {
            final var customer = customerService.findById(input.customerId()).orElseThrow(NotFoundException::new);
            final var transfer = input.transfer();

            transfer.apply(customer);

            customerService.save(customer);
            transferService.save(transfer);

            return new TransferUseCaseOutput(customer);
        });
    }

}
