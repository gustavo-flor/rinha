package com.github.gustavoflor.rinha.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.gustavoflor.rinha.core.TransferType;
import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record StatementResponse(@JsonProperty("saldo") BalanceState balanceState,
                                @JsonProperty("ultimas_transacoes") List<TransferInfo> transferInfos) {

    public static StatementResponse of(final Customer customer, final List<Transfer> transfers) {
        final var balanceState = BalanceState.of(customer);
        final var transferInfos = transfers
            .stream()
            .map(TransferInfo::of)
            .toList();
        return new StatementResponse(balanceState, transferInfos);
    }

    record BalanceState(@JsonProperty("total") Integer value,
                        @JsonProperty("data_extrato") LocalDateTime statementDate,
                        @JsonProperty("limite") Integer limite) {

        public static BalanceState of(final Customer customer) {
            return new BalanceState(customer.getBalance(), LocalDateTime.now(), customer.getLimit());
        }

    }

    @Builder
    record TransferInfo(@JsonProperty("valor") Integer value,
                        @JsonProperty("tipo") TransferType type,
                        @JsonProperty("descricao") String description,
                        @JsonProperty("realizada_em") LocalDateTime executedAt) {

        public static TransferInfo of(final Transfer transfer) {
            return TransferInfo.builder()
                .value(transfer.getValue())
                .type(transfer.getType())
                .description(transfer.getDescription())
                .executedAt(transfer.getExecutedAt())
                .build();
        }

    }

}
