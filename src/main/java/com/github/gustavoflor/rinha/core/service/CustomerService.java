package com.github.gustavoflor.rinha.core.service;

import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findById(final Integer id) {
        return customerRepository.findById(id);
    }

    public Customer save(final Customer customer) {
        return customerRepository.save(customer);
    }

}
