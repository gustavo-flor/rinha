package com.github.gustavoflor.rinha.core;

import com.github.gustavoflor.rinha.core.exception.InsufficientBalanceException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"limit\"")
    private Integer limit;

    private Integer balance;

    public void debit(final Integer value) {
        if (threshold() - value < 0) {
            throw new InsufficientBalanceException();
        }
        balance -= value;
    }

    public void credit(final Integer value) {
        balance += value;
    }

    private Integer threshold() {
        return balance + limit;
    }

}
