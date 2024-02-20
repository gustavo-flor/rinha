package com.github.gustavoflor.rinha.core.usecase;

import com.github.gustavoflor.rinha.core.TransferType;
import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;
import lombok.RequiredArgsConstructor;

public interface TransferUseCase {

    Output execute(Input input);

    @RequiredArgsConstructor
    class Input {

        private final Integer customerId;
        private final TransferType transferType;
        private final Integer value;
        private final String description;

        public Integer customerId() {
            return customerId;
        }

        public Transfer transfer() {
            return Transfer.builder()
                .customerId(customerId)
                .type(transferType)
                .value(value)
                .description(description)
                .build();
        }
    }

    @RequiredArgsConstructor
    class Output {
        private final Customer customer;

        public Customer customer() {
            return customer;
        }
    }

}
