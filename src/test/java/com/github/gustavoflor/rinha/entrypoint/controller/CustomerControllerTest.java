package com.github.gustavoflor.rinha.entrypoint.controller;

import com.github.gustavoflor.rinha.core.Transfer;
import com.github.gustavoflor.rinha.core.repository.CustomerRepository;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import com.github.gustavoflor.rinha.core.usecase.TransferUseCase;
import com.github.gustavoflor.rinha.entrypoint.ApiTest;
import com.github.gustavoflor.rinha.entrypoint.dto.TransferRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.util.CheckedCallable;

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
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequestWithType;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequestWithTypeAndValue;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequestWithValue;
import static com.github.gustavoflor.rinha.util.FakerUtil.randomTransferRequestWithValueAndDescription;
import static java.text.MessageFormat.format;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
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

    @SpyBean
    protected CustomerRepository customerRepository;

    @SpyBean
    protected TransferRepository transferRepository;

    @SpyBean
    protected LockRegistry lockRegistry;

    @AfterEach
    void afterEach() {
        Mockito.reset(customerRepository, transferRepository, lockRegistry);
        customerRepository.deleteAll();
        transferRepository.deleteAll();
    }

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
        GIVEN some requests sequentially
        WHEN get statement
        THEN should call database only one time
        """)
    void givenSomeRequestsSequentiallyWhenGetStatementThenShouldCallDatabaseOnlyOneTime() throws InterruptedException {
        final var customer = customerRepository.save(randomCustomer());
        final var customerId = customer.getId();
        final int amount = randomInteger(1, 3);

        for (int index = 0; index < amount; index++) {
            getStatement(customerId).statusCode(OK.value())
                .body(STATEMENT_BALANCE_FIELD, is(customer.getBalance()))
                .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
                .body(STATEMENT_DATE, notNullValue())
                .body(LAST_TRANSFERS_FIELD, empty());
        }

        verify(customerRepository).findById(customerId);
        verify(transferRepository).findLatest(customerId);
    }

    @Test
    @DisplayName("""
        GIVEN some requests concurrently
        WHEN get statement
        THEN should call database only one time
        """)
    void givenSomeRequestsConcurrentlyWhenGetStatementThenShouldCallDatabaseOnlyOneTime() throws InterruptedException {
        final var customer = customerRepository.save(randomCustomer());
        final var customerId = customer.getId();
        final var amount = randomInteger(1, 3);

        doSyncAndConcurrently(amount, index -> {
            getStatement(customerId).statusCode(OK.value())
                .body(STATEMENT_BALANCE_FIELD, is(customer.getBalance()))
                .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
                .body(STATEMENT_DATE, notNullValue())
                .body(LAST_TRANSFERS_FIELD, empty());
        });

        verify(customerRepository).findById(customerId);
        verify(transferRepository).findLatest(customerId);
    }

    @Test
    @DisplayName("""
        GIVEN a transfer after get statement
        WHEN call service
        THEN should invalidate cache
        """)
    void givenATransferAfterGetStatementWhenCallServiceThenShouldInvalidateCache() {
        final var customer = customerRepository.save(randomCustomer());
        final var customerId = customer.getId();
        final var request = randomTransferRequestWithValue(customer.getLimit());
        final var transferValue = request.isDebit() ? request.value() * -1 : request.value();
        final var expectedBalance = customer.getBalance() + transferValue;

        getStatement(customerId).statusCode(OK.value())
            .body(STATEMENT_BALANCE_FIELD, is(customer.getBalance()))
            .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
            .body(STATEMENT_DATE, notNullValue())
            .body(LAST_TRANSFERS_FIELD, empty());
        doTransfer(customerId, request).statusCode(OK.value());
        getStatement(customerId).statusCode(OK.value())
            .body(STATEMENT_BALANCE_FIELD, is(expectedBalance))
            .body(STATEMENT_LIMIT_FIELD, is(customer.getLimit()))
            .body(STATEMENT_DATE, notNullValue())
            .body(LAST_TRANSFERS_FIELD, hasSize(1));

        verify(customerRepository, times(3)).findById(customerId);
        verify(transferRepository, times(2)).findLatest(customerId);
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
        final var request = randomTransferRequestWithValue(randomInteger() * -1);

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
        final var request = randomTransferRequestWithValue(0);

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
        final var request = randomTransferRequestWithValue(null);

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
        final var request = randomTransferRequestWithType(null);

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
        final var request = randomTransferRequestWithDescription("");

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
        final var request = randomTransferRequestWithType(DEBIT);

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
        final var request = randomTransferRequestWithTypeAndValue(DEBIT, requestValue);
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
        final var request = randomTransferRequestWithTypeAndValue(DEBIT, requestValue);

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
        final var request = randomTransferRequestWithTypeAndValue(CREDIT, requestValue);
        final var expectedBalance = customer.getBalance() + requestValue;

        doTransfer(customer.getId(), request).statusCode(OK.value())
            .body(BALANCE_FIELD, is(expectedBalance))
            .body(LIMIT_FIELD, is(customer.getLimit()));

        final var founded = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(founded.getBalance()).isEqualTo(expectedBalance);
        assertThat(founded.getLimit()).isEqualTo(customer.getLimit());
    }

    @Test
    @DisplayName("""
        GIVEN multiple transfer requests
        WHEN try do transfer
        THEN should return ok status and update registries
        """)
    void givenMultipleTransferRequestsWhenTryDoTransferThenShouldReturnOkStatusAndUpdateRegistries() throws InterruptedException {
        final var customer = customerRepository.save(randomCustomer(10000000, 0));
        final int amount = randomInteger(10, 15);
        final var requests = new ArrayList<TransferRequest>();
        for (int i = 0; i < amount; i++) {
            final var value = randomInteger(1000, 100000);
            final var description = format("test-{0}", i);
            final var request = randomTransferRequestWithValueAndDescription(value, description);
            requests.add(request);
        }
        final var expectedBalance = requests.stream()
            .map(it -> it.isDebit() ? it.value() * -1 : it.value())
            .reduce(Integer::sum)
            .orElse(0);

        final var startedAt = now();
        doSyncAndConcurrently(amount, index -> {
            final var request = requests.get(index);
            doTransfer(customer.getId(), request).statusCode(OK.value())
                .body(LIMIT_FIELD, is(customer.getLimit()));;
        });
        final var finishedAt = now();

        final var founded = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(founded.getBalance()).isEqualTo(expectedBalance);
        assertThat(founded.getLimit()).isEqualTo(customer.getLimit());
        final var transfers = transferRepository.findAll();
        assertThat(transfers.size()).isEqualTo(amount);
        requests.forEach(request -> {
            final var transfer = transfers.stream()
                .filter(it -> it.getDescription().equals(request.description()))
                .findFirst()
                .orElseThrow();
            assertThat(transfer.getCustomerId()).isEqualTo(customer.getId());
            assertThat(transfer.getType()).isEqualTo(request.type());
            assertThat(transfer.getValue()).isEqualTo(request.value());
            assertThat(transfer.getType()).isEqualTo(request.type());
            assertThat(transfer.getExecutedAt()).isBetween(startedAt, finishedAt);
        });
    }

    @Test
    @DisplayName("""
        GIVEN a runtime exception
        WHEN try do transfer
        THEN should not commit changes on database
        """)
    void givenARuntimeExceptionWhenTryDoTransferThenShouldNotCommitChangesOnDatabase() {
        final var limit = randomInteger();
        final var customer = customerRepository.save(randomCustomer(limit, 0));
        final var request = randomTransferRequestWithValue(limit);
        doThrow(RuntimeException.class).when(transferRepository).save(any());

        doTransfer(customer.getId(), request).statusCode(INTERNAL_SERVER_ERROR.value());

        final var founded = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(founded.getBalance()).isEqualTo(customer.getBalance());
        assertThat(founded.getLimit()).isEqualTo(customer.getLimit());
        final var transfers = transferRepository.findAll();
        assertThat(transfers.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
        GIVEN an error on lock registry
        WHEN try do transfer
        THEN should return service unavailable status
        """)
    void givenAnErrorOnLockRegistryWhenTryDoTransferThenShouldReturnServiceUnavailableException() throws Exception {
        final var customer = customerRepository.save(randomCustomer());
        final var request = randomTransferRequest();
        final var lockKey = format("do-transfer.{0}", customer.getId());
        doThrow(Exception.class).when(lockRegistry)
            .executeLocked(eq(lockKey), any(), ArgumentMatchers.<CheckedCallable<TransferUseCase.Output, Exception>>any());

        doTransfer(customer.getId(), request).statusCode(SERVICE_UNAVAILABLE.value());
    }

}
