package com.github.gustavoflor.rinha.core.repository;

import com.github.gustavoflor.rinha.core.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    @Query("""
        SELECT transfer
        FROM Transfer transfer
        WHERE transfer.customerId = :customerId
        ORDER BY transfer.executedAt DESC
        LIMIT 10
        """)
    List<Transfer> findLatest(Integer customerId);

}
