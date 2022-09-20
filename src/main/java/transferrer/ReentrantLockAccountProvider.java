package transferrer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockAccountProvider implements LockProvider<Integer> {

    private final Map<Integer, Lock> lockStorage;

    public ReentrantLockAccountProvider() {
        this(new ConcurrentHashMap<>());
    }

    ReentrantLockAccountProvider(Map<Integer, Lock> lockStorage) {
        this.lockStorage = lockStorage;
    }

    @Override
    public Lock getLock(Integer id) {
        // thread safe
        return lockStorage.compute(id, ((key, lock) -> lock != null ? lock : new ReentrantLock()));
    }

}
