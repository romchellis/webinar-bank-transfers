package transferrer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

public class SyncronizedBankTransferrerTest {

    private static final BigInteger sum = BigInteger.valueOf(1000);
    public static final int N_THREADS = 100;

    private BankTransferrer bankTransferrer;
    private BankTransferrer notThreadSafeTransferrer;

    @BeforeEach
    void setUp() {
        notThreadSafeTransferrer = new MockTransferrer();
        bankTransferrer = new SyncronizedBankTransferrer();
    }

    @Test
    void should_get_expected_sums_in_both_accounts() {
        var from = new Account(BigInteger.valueOf(1000), 1);
        var to = new Account(BigInteger.valueOf(2000), 2);

        bankTransferrer.transfer(from, to, sum);

        assertThat(from.getSum()).isEqualTo(BigInteger.ZERO);
        assertThat(to.getSum()).isEqualTo(BigInteger.valueOf(3000));
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

    static class MockTransferrer implements BankTransferrer {

        @Override
        public void transfer(Account from, Account to, BigInteger sum) {
            from.credit(sum);
            to.debit(sum);
        }

    }
}