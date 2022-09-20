package transferrer;

import java.util.concurrent.locks.Lock;

public interface LockProvider<T> {

    Lock getLock(T id);

}
