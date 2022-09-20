package transferrer;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class Account {

    private BigInteger sum = BigInteger.ZERO;
    private Integer id;

    public Account(BigInteger sum, Integer id) {
        Objects.requireNonNull(sum, "Sum cannot be null!");
        this.sum = sum;
        this.id = id;
    }

    public BigInteger credit(BigInteger debit) {
        Objects.requireNonNull(debit, "Sum cannot be null!");

        this.sum = sum.subtract(debit);
        return this.sum;
    }

    public BigInteger debit(BigInteger credit) {
        Objects.requireNonNull(credit, "Sum cannot be null!");

        this.sum = sum.add(credit);
        return this.sum;
    }

    public Integer getId() {
        return id;
    }

    public BigInteger getSum() {
        return sum;
    }
}
