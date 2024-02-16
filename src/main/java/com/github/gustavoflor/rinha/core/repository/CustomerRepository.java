package com.github.gustavoflor.rinha.core.repository;

import com.github.gustavoflor.rinha.core.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
