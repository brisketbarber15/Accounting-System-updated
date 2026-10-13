package statics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import system.Transaction;

/** The book of original entry: every posted transaction in the order it was recorded. */
public class GeneralJournal {
    private final List<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction){
        transactions.add(transaction);
    }

    public boolean removeLast(){
        if(transactions.isEmpty()){
            return false;
        }
        transactions.remove(transactions.size() - 1);
        return true;
    }

    public void clear(){
        transactions.clear();
    }

    public int size(){
        return transactions.size();
    }

    public List<Transaction> getTransactions(){
        return Collections.unmodifiableList(transactions);
    }
}
