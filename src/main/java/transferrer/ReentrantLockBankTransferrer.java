package transferrer;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// jvm proccesse
// distributed lock provider
// redis
public class ReentrantLockBankTransferrer implements BankTransferrer {

    private final LockProvider<Integer> lockProvider;
    private final BankTransferrer originBankTransferrer;

    public ReentrantLockBankTransferrer(LockProvider<Integer> lockProvider) {
        this(lockProvider, new SimpleBankTransferrer());
    }

    ReentrantLockBankTransferrer(LockProvider<Integer> lockProvider,
                                 BankTransferrer originBankTransferrer) {
        this.lockProvider = Objects.requireNonNull(lockProvider);
        this.originBankTransferrer = Objects.requireNonNull(originBankTransferrer);
    }

    @Override
    public void transfer(Account from, Account to, BigInteger sum) {
        Lock lockFrom = lockProvider.getLock(from.getId());
        Lock lockTo = lockProvider.getLock(to.getId());


        if (from.getId() > to.getId()) {
            lockFrom.lock();
            lockTo.lock();
            originBankTransferrer.transfer(from, to, sum);
            lockTo.unlock();
            lockFrom.unlock();
        } else {
            lockTo.lock();
            lockFrom.lock();
            originBankTransferrer.transfer(from, to, sum);
            lockFrom.unlock();
            lockTo.unlock();
        }
    }
}
