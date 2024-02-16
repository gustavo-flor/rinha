package com.github.gustavoflor.rinha.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TransferType {
    CREDIT("c") {
        @Override
        public void transfer(final Customer customer, final Integer value) {
            customer.credit(value);
        }
    },
    DEBIT("d") {
        @Override
        public void transfer(final Customer customer, final Integer value) {
            customer.debit(value);
        }
    };

    @JsonValue
    private final String code;

    @JsonCreator
    public static TransferType of(final String code) {
        return Arrays.stream(values())
            .filter(it -> it.code.equals(code))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public abstract void transfer(Customer customer, Integer value);

}
