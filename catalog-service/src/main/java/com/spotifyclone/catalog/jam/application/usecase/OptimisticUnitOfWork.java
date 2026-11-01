package com.spotifyclone.catalog.jam.application.usecase;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Transaction boundary + optimistic-lock retry for the jam use cases (POC-1:
 * {@code @Version} + retry → no lost updates).
 *
 * <p>The conflict surfaces at <em>commit</em> time (Hibernate flushes the
 * versioned UPDATE there), so a retry placed inside a declarative
 * {@code @Transactional} method could never catch it. This helper therefore
 * owns the transaction programmatically and re-runs the <em>whole</em> unit —
 * reload, re-apply domain rules, re-append outbox events — on
 * {@link OptimisticLockingFailureException}. Domain and validation exceptions
 * are never retried. The aggregate's idempotency keys keep a replayed attempt
 * a no-op (POC-3), so re-execution is safe.
 */
@Component
public class OptimisticUnitOfWork {

    private static final int MAX_ATTEMPTS = 3;

    private final TransactionTemplate transactionTemplate;

    public OptimisticUnitOfWork(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public <T> T execute(Supplier<T> work) {
        int attempt = 0;
        while (true) {
            try {
                return transactionTemplate.execute(status -> work.get());
            } catch (OptimisticLockingFailureException conflict) {
                attempt++;
                if (attempt >= MAX_ATTEMPTS) {
                    throw conflict;
                }
            }
        }
    }

    public void executeWithoutResult(Runnable work) {
        execute(() -> {
            work.run();
            return null;
        });
    }
}
