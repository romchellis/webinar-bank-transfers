package transferrer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockBankTransferrerTest {

    private BankTransferrer bankTransferrer;
    private LockProvider<Integer> lockProvider;

    public static final int N_THREADS = 100;
    private static final BigInteger sum = BigInteger.valueOf(500);

    @BeforeEach
    void setUp() {
        lockProvider = new ThreadSafeLockAccountProviderMock();
        bankTransferrer = new ReentrantLockBankTransferrer(lockProvider);
    }

    @Test
    void should_get_expected_sums_in_both_accounts() {
        var from = new Account(BigInteger.valueOf(1000), 1);
        var to = new Account(BigInteger.valueOf(2000), 2);

        bankTransferrer.transfer(from, to, sum);

        assertThat(from.getSum()).isEqualTo(BigInteger.valueOf(500));
        assertThat(to.getSum()).isEqualTo(BigInteger.valueOf(2500));
    }

    @Test
    @Timeout(2)
    void should_stay_consintent_sum_between_two_accounts_in_parrallel_execution() throws InterruptedException {
        var from = new Account(BigInteger.valueOf(50), 1);
        var to = new Account(BigInteger.valueOf(50), 2);
        var executorService = Executors.newFixedThreadPool(N_THREADS);
        var barrier = new CyclicBarrier(N_THREADS);
        var latch = new CountDownLatch(N_THREADS);

        for (int i = 0; i < N_THREADS / 2; i++) {
            executorService.submit(() -> {
                await(barrier);
                bankTransferrer.transfer(from, to, BigInteger.valueOf(1));
                latch.countDown();
            });
        }

        for (int i = 0; i < N_THREADS / 2; i++) {
            executorService.submit(() -> {
                await(barrier);
                bankTransferrer.transfer(to, from, BigInteger.valueOf(1));
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();
        BigInteger fromResult = from.getSum();
        BigInteger toResult = to.getSum();
        BigInteger result = fromResult.add(toResult);

        assertThat(result).isEqualTo(BigInteger.valueOf(100));
    }

    private void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private class ThreadSafeLockAccountProviderMock implements LockProvider<Integer> {

        private final Map<Integer, Lock> lockStorage = new ConcurrentHashMap<>();

        @Override
        public Lock getLock(Integer id) {
            return lockStorage.compute(id, ((key, lock) -> lock != null ? lock : new ReentrantLock()));
        }
    }
}