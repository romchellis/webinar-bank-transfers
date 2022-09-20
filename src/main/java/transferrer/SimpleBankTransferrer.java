package transferrer;

import java.math.BigInteger;
import java.util.Objects;

public class SimpleBankTransferrer implements BankTransferrer {

    @Override
    public void transfer(Account from, Account to, BigInteger sum) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(sum);

        from.credit(sum);
        to.debit(sum);
    }

}
