package com.github.gustavoflor.rinha.core;

import com.github.gustavoflor.rinha.core.exception.InsufficientBalanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.gustavoflor.rinha.core.TransferType.DEBIT;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomCustomer;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomInteger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TransferTypeTest {

    @Test
    @DisplayName("""
        GIVEN an invalid debit value
        WHEN debit transfer
        THEN should throw InsufficientBalanceException
        """)
    void givenAnInvalidDebitValueWhenDebitTransferThenShouldThrowInsufficientBalanceException() {
        final int limit = randomInteger();
        final var customer = randomCustomer(limit, 0);
        final int debitValue = limit + 1;

        assertThatThrownBy(() -> DEBIT.transfer(customer, debitValue)).isInstanceOf(InsufficientBalanceException.class);
    }

}
