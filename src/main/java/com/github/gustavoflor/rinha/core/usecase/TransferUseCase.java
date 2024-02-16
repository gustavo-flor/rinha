package com.github.gustavoflor.rinha.core.usecase;

import com.github.gustavoflor.rinha.core.TransferType;
import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;

public interface TransferUseCase {

    Output execute(Input input);

    record Input(Integer customerId, TransferType transferType, Integer value, String description) {

        public Transfer transfer() {
            return Transfer.builder()
                .customerId(customerId)
                .type(transferType)
                .value(value)
                .description(description)
                .build();
        }

    }

    record Output(Customer customer) {}

}
