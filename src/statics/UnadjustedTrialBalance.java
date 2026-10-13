package statics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import system.Account;
import system.NormalBalance;
import system.TAccount;

/** Lists every account with a balance in its debit or credit column. */
public class UnadjustedTrialBalance {
    public record Row(Account account, BigDecimal debit, BigDecimal credit) {}

    private final List<Row> rows = new ArrayList<>();
    private BigDecimal totalDebits = BigDecimal.ZERO;
    private BigDecimal totalCredits = BigDecimal.ZERO;

    public UnadjustedTrialBalance(GeneralLedger ledger){
        for(TAccount taccount : ledger.getAll()){
            BigDecimal net = taccount.getNetBalance();
            if(net.signum() == 0){
                continue;
            }
            Account account = taccount.getAccount();
            boolean normalIsDebit = account.getNormalBalance() == NormalBalance.DEBIT;
            boolean onDebitSide = (net.signum() > 0) == normalIsDebit;
            BigDecimal amount = net.abs();
            if(onDebitSide){
                rows.add(new Row(account, amount, null));
                totalDebits = totalDebits.add(amount);
            }else{
                rows.add(new Row(account, null, amount));
                totalCredits = totalCredits.add(amount);
            }
        }
    }

    public List<Row> getRows(){
        return rows;
    }

    public BigDecimal getTotalDebits(){
        return totalDebits;
    }

    public BigDecimal getTotalCredits(){
        return totalCredits;
    }

    public boolean isBalanced(){
        return totalDebits.compareTo(totalCredits) == 0;
    }
}
