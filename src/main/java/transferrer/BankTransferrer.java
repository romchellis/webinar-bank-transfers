package transferrer;

import java.math.BigInteger;

public interface BankTransferrer {

    //API FIRST

    void transfer(Account from, Account to, BigInteger sum);

}
