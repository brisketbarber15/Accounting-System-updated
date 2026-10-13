/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system;

/**
 *
 * @author Ryan
 */
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.swing.JOptionPane;

public class TAccount {
    private final Account account;
    private final List<JournalEntryItem> debits = new ArrayList<>();
    private final List<JournalEntryItem> credits = new ArrayList<>();

    public TAccount(Account account){
        this.account = account;
    }

    public void addEntry(JournalEntryItem item){
        if(!item.account().equals(this.account)){
            JOptionPane.showMessageDialog(null, "Incompatible account title: expected: " + this.account.name() + "; received: " + item.account().name());
            return;
        }
        if(item.entryType() == EntryType.DEBIT){
            debits.add(item);
        }else{
            credits.add(item);
        }
    }

    public BigDecimal getNetBalance(){
        BigDecimal totalDebits = debits.stream().map(JournalEntryItem::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredits = credits.stream().map(JournalEntryItem::amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return account.getNormalBalance() == NormalBalance.DEBIT ? totalDebits.subtract(totalCredits) : totalCredits.subtract(totalDebits);
    }

    public Account getAccount(){ 
        return account; 
    }
    
    public List<JournalEntryItem> getDebits(){ 
        return debits; 
    }
    
    public List<JournalEntryItem> getCredits(){ 
        return credits; 
    }
}
