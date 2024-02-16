package com.github.gustavoflor.rinha.entrypoint.controller;

import com.github.gustavoflor.rinha.core.usecase.StatementUseCase;
import com.github.gustavoflor.rinha.entrypoint.dto.StatementResponse;
import com.github.gustavoflor.rinha.entrypoint.dto.TransferRequest;
import com.github.gustavoflor.rinha.entrypoint.dto.TransferResponse;
import com.github.gustavoflor.rinha.core.usecase.TransferUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class CustomerController {

    private final StatementUseCase statementUseCase;
    private final TransferUseCase transferUseCase;

    @GetMapping("/{id}/extrato")
    @ResponseStatus(OK)
    public StatementResponse getStatement(@PathVariable final Integer id) {
        final var input = new StatementUseCase.Input(id);
        final var output = statementUseCase.execute(input);
        return StatementResponse.of(output.customer(), output.latestTransfers());
    }

    @PostMapping("/{id}/transacoes")
    @ResponseStatus(OK)
    public TransferResponse doTransfer(@PathVariable final Integer id, @Valid @RequestBody final TransferRequest request) {
        final var input = new TransferUseCase.Input(id, request.type(), request.value(), request.description());
        final var output = transferUseCase.execute(input);
        return TransferResponse.of(output.customer());
    }

}
