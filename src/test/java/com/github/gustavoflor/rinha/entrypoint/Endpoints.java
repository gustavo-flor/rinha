package com.github.gustavoflor.rinha.entrypoint;

import com.github.gustavoflor.rinha.entrypoint.dto.TransferRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Endpoints {

    @NoArgsConstructor(access = PRIVATE)
    public static class CustomerController {

        public static ValidatableResponse getStatement(final int customerId) {
            return RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/clientes/{id}/extrato", customerId)
                .then();
        }

        public static ValidatableResponse doTransfer(final int customerId, final TransferRequest request) {
            return RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .post("/clientes/{id}/transacoes", customerId)
                .then();
        }

    }

}
