package com.github.gustavoflor.rinha.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.Callable;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRegistry lockRegistry;

    public <R> R tryLock(final String lockKey, final Duration duration, final Callable<R> callable) throws Exception {
        return lockRegistry.executeLocked(lockKey, duration, callable::call);
    }

}
