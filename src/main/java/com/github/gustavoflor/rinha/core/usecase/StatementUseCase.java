package com.github.gustavoflor.rinha.core.usecase;

import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;
import lombok.RequiredArgsConstructor;

import java.util.List;

public interface StatementUseCase {

    Output execute(Input input);

    @RequiredArgsConstructor
    class Input {
        private final Integer customerId;

        public Integer customerId() {
            return customerId;
        }
    }

    @RequiredArgsConstructor
    class Output {
        private final Customer customer;
        private final List<Transfer> latestTransfers;

        public Customer customer() {
            return customer;
        }

        public List<Transfer> latestTransfers() {
            return latestTransfers;
        }
    }

}
