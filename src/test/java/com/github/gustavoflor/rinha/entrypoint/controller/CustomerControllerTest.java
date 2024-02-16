package com.github.gustavoflor.rinha.entrypoint.controller;

import com.github.gustavoflor.rinha.core.Transfer;
import com.github.gustavoflor.rinha.entrypoint.ApiTest;
import com.github.gustavoflor.rinha.util.FakerUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.github.gustavoflor.rinha.core.TransferType.CREDIT;
import static com.github.gustavoflor.rinha.core.TransferType.DEBIT;
import static com.github.gustavoflor.rinha.entrypoint.Endpoints.CustomerController.doTransfer;
import static com.github.gustavoflor.rinha.entrypoint.Endpoints.CustomerController.getStatement;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomCustomer;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomInteger;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransfer;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequest;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequestWithDescription;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

class CustomerControllerTest extends ApiTest {

    private static final String BALANCE_FIELD = "saldo";
    private static final String LIMIT_FIELD = "limite";
    private static final String LAST_TRANSFERS_FIELD = "ultimas_transacoes";
    private static final String STATEMENT_LIMIT_FIELD = "saldo.limite";
    private static final String STATEMENT_BALANCE_FIELD = "saldo.total";
    private static final String STATEMENT_DATE = "saldo.data_extrato";
    private static final String LAST_TRANSFER_VALUE_FIELD_TEMPLATE = LAST_TRANSFERS_FIELD + "[{0}].valor";
    private static final String LAST_TRANSFER_TYPE_FIELD_TEMPLATE = LAST_TRANSFERS_FIELD + "[{0}].tipo";
    private static final String LAST_TRANSFER_DESCRIPTION_FIELD_TEMPLATE = LAST_TRANSFERS_FIELD + "[{0}].descricao";
    private static final String LAST_TRANSFER_EXECUTED_AT_FIELD_TEMPLATE = LAST_TRANSFERS_FIELD + "[{0}].realizada_em";

    @Test
    @DisplayName("""
        GIVEN an unknown customer ID
        WHEN try get statement
        THEN should return not found status
        """)
    void giveAnUnknownCustomerIdWhenTryGetStatementThenShouldReturnNotFoundStatus() {
        final var customerId = randomInteger();

        getStatement(customerId).statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("""
        GIVEN a new customer
        WHEN try get statement
        THEN should return a statement with empty last transfers field
        """)
    void givenANewCustomerWhenTryGetStatementThenShouldReturnAStatementWithEmptyLastTransfersField() {
        final var customer = customerRepository.save(randomCustomer());

        getStatement(customer.getId()).statusCode(OK.value())
            .body(STATEMENT_BALANCE_FIELD, is(customer.getBalance()))
            .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
            .body(STATEMENT_DATE, notNullValue())
            .body(LAST_TRANSFERS_FIELD, empty());
    }

    @Test
    @DisplayName("""
        GIVEN a customer with less than 10 transfers
        WHEN try get statement
        THEN should return a statement with last transfers field filled
        """)
    void givenACustomerWithLessThanTenTransfersWhenTryGetStatementThenShouldReturnAStatementWithLastTransfersFieldFilled() {
        final var customer = customerRepository.save(randomCustomer());
        final var customerId = customer.getId();
        final var lastTransfers = new ArrayList<Transfer>();
        for (int i = 0; i < randomInteger(1, 9); i++) {
            final var transfer = transferRepository.save(randomTransfer(customerId));
            lastTransfers.add(transfer);
        }

        final var response = getStatement(customerId).statusCode(OK.value())
            .body(STATEMENT_BALANCE_FIELD, is(customer.getBalance()))
            .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
            .body(STATEMENT_DATE, notNullValue())
            .body(LAST_TRANSFERS_FIELD, hasSize(lastTransfers.size()));

        for (int i = 0; i < lastTransfers.size(); i++) {
            final var transfer = lastTransfers.get(lastTransfers.size() - 1 - i);
            response.body(format(LAST_TRANSFER_VALUE_FIELD_TEMPLATE, i), is(transfer.getValue()));
            response.body(format(LAST_TRANSFER_TYPE_FIELD_TEMPLATE, i), is(transfer.getType().getCode()));
            response.body(format(LAST_TRANSFER_DESCRIPTION_FIELD_TEMPLATE, i), is(transfer.getDescription()));
            response.body(format(LAST_TRANSFER_EXECUTED_AT_FIELD_TEMPLATE, i), notNullValue());
        }
    }

    @Test
    @DisplayName("""
        GIVEN a customer with 10 or more transfers
        WHEN try get statement
        THEN should return a statement with only 10 last transfers field
        """)
    void givenACustomerWithTenOrMoreTransfersWhenTryGetStatementThenShouldReturnAStatementWithOnlyTenLastTransfersField() {
        final var customer = customerRepository.save(randomCustomer());
        final var customerId = customer.getId();
        for (int i = 0; i < randomInteger(0, 10); i++) {
            transferRepository.save(randomTransfer(customerId));
        }
        final var lastTransfers = new ArrayList<Transfer>();
        for (int i = 0; i < 10; i++) {
            final var transfer = transferRepository.save(randomTransfer(customerId));
            lastTransfers.add(transfer);
        }

        final var response = getStatement(customerId).statusCode(OK.value())
            .body(STATEMENT_BALANCE_FIELD, is(customer.getBalance()))
            .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
            .body(STATEMENT_DATE, notNullValue())
            .body(LAST_TRANSFERS_FIELD, hasSize(10));

        for (int i = 0; i < 10; i++) {
            final var transfer = lastTransfers.get(9 - i);
            response.body(format(LAST_TRANSFER_VALUE_FIELD_TEMPLATE, i), is(transfer.getValue()));
            response.body(format(LAST_TRANSFER_TYPE_FIELD_TEMPLATE, i), is(transfer.getType().getCode()));
            response.body(format(LAST_TRANSFER_DESCRIPTION_FIELD_TEMPLATE, i), is(transfer.getDescription()));
            response.body(format(LAST_TRANSFER_EXECUTED_AT_FIELD_TEMPLATE, i), notNullValue());
        }
    }


    @Test
    @DisplayName("""
        GIVEN an unknown customer ID
        WHEN try do transfer
        THEN should return not found status
        """)
    void givenAnUnknownCustomerIdWhenTryDoTransferThenShouldReturnNotFoundStatus() {
        final var customerId = randomInteger();
        final var request = randomTransferRequest();

        doTransfer(customerId, request).statusCode(NOT_FOUND.value());
    }

    @Test
    @DisplayName("""
        GIVEN a negative value
        WHEN try do transfer
        THEN should return bad request status
        """)
    void givenANegativeValueWhenTryDoTransferThenShouldReturnBadRequestStatus() {
        final var customerId = randomInteger();
        final var request = FakerUtil.randomTransferRequestWithValue(randomInteger() * -1);

        doTransfer(customerId, request).statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("""
        GIVEN a zero value
        WHEN try do transfer
        THEN should return bad request status
        """)
    void givenAZeroValueWhenTryDoTransferThenShouldReturnBadRequestStatus() {
        final var customerId = randomInteger();
        final var request = FakerUtil.randomTransferRequestWithValue(0);

        doTransfer(customerId, request).statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("""
        GIVEN a null value
        WHEN try do transfer
        THEN should return bad request status
        """)
    void givenANullValueWhenTryDoTransferThenShouldReturnBadRequestStatus() {
        final var customerId = randomInteger();
        final var request = FakerUtil.randomTransferRequestWithValue(null);

        doTransfer(customerId, request).statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("""
        GIVEN a null type
        WHEN try do transfer
        THEN should return bad request status
        """)
    void givenANullTypeWhenTryDoTransferThenShouldReturnBadRequestStatus() {
        final var customerId = randomInteger();
        final var request = FakerUtil.randomTransferRequestWithType(null);

        doTransfer(customerId, request).statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("""
        GIVEN an empty description
        WHEN try do transfer
        THEN should return bad request status
        """)
    void givenAnEmptyDescriptionWhenTryDoTransferThenShouldReturnBadRequestStatus() {
        final var customerId = randomInteger();
        final var request = FakerUtil.randomTransferRequestWithDescription("");

        doTransfer(customerId, request).statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("""
        GIVEN a null description
        WHEN try do transfer
        THEN should return bad request status
        """)
    void givenANullDescriptionWhenTryDoTransferThenShouldReturnBadRequestStatus() {
        final var customerId = randomInteger();
        final var request = randomTransferRequestWithDescription(null);

        doTransfer(customerId, request).statusCode(BAD_REQUEST.value());
    }

    @Test
    @DisplayName("""
        GIVEN a too high debit request
        WHEN try do transfer
        THEN should return unprocessable entity status
        """)
    void givenATooHighDebitRequestWhenTryDoTransferThenShouldReturnUnprocessableEntityStatus() {
        final var customer = customerRepository.save(randomCustomer(0, 0));
        final var request = FakerUtil.randomTransferRequestWithType(DEBIT);

        doTransfer(customer.getId(), request).statusCode(UNPROCESSABLE_ENTITY.value());
    }

    @Test
    @DisplayName("""
        GIVEN a debit request
        WHEN try do transfer
        THEN should return ok status and update registry
        """)
    void givenADebitRequestWhenTryDoTransferThenShouldReturnOkStatusAndUpdateRegistry() {
        final var requestValue = randomInteger();
        final var customer = customerRepository.save(randomCustomer(randomInteger(), randomInteger() + requestValue));
        final var request = FakerUtil.randomTransferRequestWithTypeAndValue(DEBIT, requestValue);
        final var expectedBalance = customer.getBalance() - requestValue;

        doTransfer(customer.getId(), request).statusCode(OK.value())
            .body(BALANCE_FIELD, is(expectedBalance))
            .body(LIMIT_FIELD, is(customer.getLimit()));

        final var founded = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(founded.getBalance()).isEqualTo(expectedBalance);
        assertThat(founded.getLimit()).isEqualTo(customer.getLimit());
    }

    @Test
    @DisplayName("""
        GIVEN a full debit request
        WHEN try do transfer
        THEN should return ok status and update registry
        """)
    void givenAFullDebitRequestWhenTryDoTransferThenShouldReturnOkStatusAndUpdateRegistry() {
        final var customer = customerRepository.save(randomCustomer(0, randomInteger()));
        final var requestValue = customer.getBalance();
        final var request = FakerUtil.randomTransferRequestWithTypeAndValue(DEBIT, requestValue);

        doTransfer(customer.getId(), request).statusCode(OK.value())
            .body(BALANCE_FIELD, is(0))
            .body(LIMIT_FIELD, is(0));

        final var founded = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(founded.getBalance()).isZero();
        assertThat(founded.getLimit()).isZero();
    }

    @Test
    @DisplayName("""
        GIVEN a credit request
        WHEN try do transfer
        THEN should return ok status and update registry
        """)
    void givenACreditRequestWhenTryDoTransferThenShouldReturnOkStatusAndUpdateRegistry() {
        final var requestValue = randomInteger();
        final var customer = customerRepository.save(randomCustomer());
        final var request = FakerUtil.randomTransferRequestWithTypeAndValue(CREDIT, requestValue);
        final var expectedBalance = customer.getBalance() + requestValue;

        doTransfer(customer.getId(), request).statusCode(OK.value())
            .body(BALANCE_FIELD, is(expectedBalance))
            .body(LIMIT_FIELD, is(customer.getLimit()));

        final var founded = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(founded.getBalance()).isEqualTo(expectedBalance);
        assertThat(founded.getLimit()).isEqualTo(customer.getLimit());
    }

}
