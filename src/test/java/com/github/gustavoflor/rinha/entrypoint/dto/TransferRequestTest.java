package com.github.gustavoflor.rinha.entrypoint.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.gustavoflor.rinha.core.TransferType.CREDIT;
import static com.github.gustavoflor.rinha.core.TransferType.DEBIT;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequestWithType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TransferRequestTest {

    @Test
    @DisplayName("""
        GIVEN a debit transfer request
        WHEN check if is debit
        THEN should return true
        """)
    void givenADebitTransferRequestWhenCheckIfIsDebitThenShouldReturnTrue() {
        final var transferRequest = randomTransferRequestWithType(DEBIT);

        final var isDebit = transferRequest.isDebit();

        assertThat(isDebit).isTrue();
    }

    @Test
    @DisplayName("""
        GIVEN a credit transfer request
        WHEN check if is debit
        THEN should return false
        """)
    void givenACreditTransferRequestWhenCheckIfIsDebitThenShouldReturnFalse() {
        final var transferRequest = randomTransferRequestWithType(CREDIT);

        final var isDebit = transferRequest.isDebit();

        assertThat(isDebit).isFalse();
    }

}
