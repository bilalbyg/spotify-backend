package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.domain.exception.NotHostException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The optimistic-lock conflict surfaces at commit time (POC-1), so the retry
 * must wrap the whole transaction — reload, re-apply, re-commit — not just the
 * save call. A domain or validation exception must never be retried.
 */
class OptimisticUnitOfWorkTest {

    static OptimisticUnitOfWork unitOfWork() {
        PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
        when(transactionManager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        return new OptimisticUnitOfWork(transactionManager);
    }

    @Test
    void runsWorkAndReturnsResult() {
        assertThat(unitOfWork().execute(() -> "ok")).isEqualTo("ok");
    }

    @Test
    void retriesAfterOptimisticLockConflict() {
        AtomicInteger attempts = new AtomicInteger();

        String result = unitOfWork().execute(() -> {
            if (attempts.incrementAndGet() == 1) {
                throw new OptimisticLockingFailureException("row was updated concurrently");
            }
            return "ok";
        });

        assertThat(result).isEqualTo("ok");
        assertThat(attempts).hasValue(2);
    }

    @Test
    void givesUpAfterMaxAttempts() {
        AtomicInteger attempts = new AtomicInteger();

        assertThatThrownBy(() -> unitOfWork().execute(() -> {
            attempts.incrementAndGet();
            throw new OptimisticLockingFailureException("always conflicting");
        })).isInstanceOf(OptimisticLockingFailureException.class);

        assertThat(attempts).hasValue(3);
    }

    @Test
    void doesNotRetryDomainExceptions() {
        AtomicInteger attempts = new AtomicInteger();

        assertThatThrownBy(() -> unitOfWork().execute(() -> {
            attempts.incrementAndGet();
            throw new NotHostException("not the host");
        })).isInstanceOf(NotHostException.class);

        assertThat(attempts).hasValue(1);
    }
}
