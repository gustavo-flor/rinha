package com.github.gustavoflor.rinha.entrypoint;

import com.github.gustavoflor.rinha.Application;
import com.github.gustavoflor.rinha.CoreTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTest extends CoreTest {

    @LocalServerPort
    private String port;

    @BeforeEach
    void beforeEach() throws IOException {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("""
        GIVEN a local server port
        WHEN start API test
        THEN should not be blank
        """)
    void givenALocalServerPortWhenStartAPITestThenShouldNotBeBlank() {
        assertThat(port).isNotBlank();
    }

}
