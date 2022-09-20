package transferrer;

import java.math.BigInteger;

public class SyncronizedBankTransferrer implements BankTransferrer {

    private final BankTransferrer originBankTransferrer;

    public SyncronizedBankTransferrer() {
        this(new SimpleBankTransferrer());
    }

    SyncronizedBankTransferrer(BankTransferrer originBankTransferrer) {
        this.originBankTransferrer = originBankTransferrer;
    }

    @Override
    public void transfer(Account from, Account to, BigInteger sum) {
        int idFrom = from.getId();
        int idTo = to.getId();

        if (idFrom > idTo) {
            synchronized (from) {
                synchronized (to) {
                    originBankTransferrer.transfer(from, to, sum);
                }
            }
        } else {
            synchronized (to) {
                synchronized (from) {
                    originBankTransferrer.transfer(from, to, sum);
                }
            }
        }
    }

}
