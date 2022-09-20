package transferrer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.swing.*;

public class SimpleBankTransferrerTest {

    private BankTransferrer bankTransferrer;

    @BeforeEach
    void setUp() {
        bankTransferrer = new SimpleBankTransferrer();
    }

    @Test
    void should_decrease_sum_first_account_and_increase_sum_second_after_transfer() {
        var fromStartSum = BigInteger.valueOf(1000);
        var toStartSum = BigInteger.valueOf(2000);
        var from = new Account(fromStartSum, 1);
        var to = new Account(toStartSum, 2);
        var sum = BigInteger.valueOf(100);

        bankTransferrer.transfer(from, to, sum);
        assertThat(from.getSum()).isEqualTo(fromStartSum.subtract(sum));
        assertThat(to.getSum()).isEqualTo(toStartSum.add(sum));
    }
}