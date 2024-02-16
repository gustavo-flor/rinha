package com.github.gustavoflor.rinha;

import com.github.gustavoflor.rinha.core.repository.CustomerRepository;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public abstract class CoreTest {

    @SpyBean
    protected CustomerRepository customerRepository;

    @SpyBean
    protected TransferRepository transferRepository;

    @AfterEach
    void afterEach() {
        Mockito.reset(customerRepository, transferRepository);
        customerRepository.deleteAll();
        transferRepository.deleteAll();
    }

}
