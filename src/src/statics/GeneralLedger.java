package statics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import system.Account;
import system.JournalEntryItem;
import system.TAccount;
import system.Transaction;

/** Aggregates the opening balances and all transactions into one T-account per account. */
public class GeneralLedger {
    private final Map<Integer, TAccount> taccounts = new LinkedHashMap<>();

    public GeneralLedger(List<Account> accounts, Transaction opening, List<Transaction> transactions){
        for(Account account : accounts){
            taccounts.put(account.code(), new TAccount(account));
        }
        addTransaction(opening);
        for(Transaction transaction : transactions){
            addTransaction(transaction);
        }
    }

    private void addTransaction(Transaction transaction){
        if(transaction == null){
            return;
        }
        for(JournalEntryItem item : transaction.getItems()){
            taccounts.get(item.account().code()).addEntry(item);
        }
    }

    public TAccount get(int code){
        return taccounts.get(code);
    }

    public Collection<TAccount> getAll(){
        return taccounts.values();
    }

    /** T-accounts that have at least one entry. */
    public List<TAccount> getActive(){
        List<TAccount> active = new ArrayList<>();
        for(TAccount taccount : taccounts.values()){
            if(!taccount.getDebits().isEmpty() || !taccount.getCredits().isEmpty()){
                active.add(taccount);
            }
        }
        return active;
    }

    /** Balance on the account's normal side (a contra account is the opposite side of its parent). */
    public BigDecimal balance(int code){
        return taccounts.get(code).getNetBalance();
    }
}
