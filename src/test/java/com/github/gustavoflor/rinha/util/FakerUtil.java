package com.github.gustavoflor.rinha.util;

import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;
import com.github.gustavoflor.rinha.core.TransferType;
import com.github.gustavoflor.rinha.entrypoint.dto.TransferRequest;
import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;

import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class FakerUtil {

    private static final Faker FAKER = new Faker();

    public static Customer randomCustomer() {
        return randomCustomer(randomInteger(), randomInteger(0));
    }

    public static Customer randomCustomer(final int limit, final int balance) {
        return Customer.builder()
            .limit(limit)
            .balance(balance)
            .build();
    }

    public static Transfer randomTransfer(final int customerId) {
        return Transfer.builder()
            .customerId(customerId)
            .type(randomTransferType())
            .value(randomInteger())
            .description(randomDescription())
            .executedAt(now())
            .build();
    }

    public static TransferType randomTransferType() {
        return FAKER.options().option(TransferType.class);
    }

    public static String randomDescription() {
        return FAKER.bothify("?????-####");
    }

    public static TransferRequest randomTransferRequest() {
        return randomTransferRequest(randomTransferType());
    }

    public static TransferRequest randomTransferRequest(final TransferType transferType) {
        return randomTransferRequest(transferType, randomInteger());
    }

    public static TransferRequest randomTransferRequest(final TransferType transferType, final Integer value) {
        return TransferRequest.builder()
            .value(value)
            .type(transferType)
            .description(randomDescription())
            .build();
    }

    public static Integer randomInteger() {
        return randomInteger(1000);
    }

    public static Integer randomInteger(final int min) {
        return randomInteger(min, 10000000);
    }

    public static Integer randomInteger(final int min, final int max) {
        return FAKER.number().numberBetween(min, max);
    }

}
