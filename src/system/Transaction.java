/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system;

/**
 *
 * @author Ryan
 */
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.math.BigDecimal;
import javax.swing.JOptionPane;

public class Transaction {
    private final UUID id;
    private final LocalDate date;
    private final String description;
    private final List<JournalEntryItem> items;

    public Transaction(UUID id, LocalDate date, String description, List<JournalEntryItem> items){
        this.id = Objects.requireNonNull(id);
        this.date = Objects.requireNonNull(date);
        this.description = Objects.requireNonNull(description);
        this.items = List.copyOf(Objects.requireNonNull(items));
        validDoubleEntry();
    }

    private void validDoubleEntry(){
        BigDecimal totalDebits = items.stream()
            .filter(item -> item.entryType() == EntryType.DEBIT)
            .map(JournalEntryItem::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = items.stream()
            .filter(item -> item.entryType() == EntryType.CREDIT)
            .map(JournalEntryItem::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(totalDebits.compareTo(totalCredits) != 0){
            JOptionPane.showMessageDialog(null, "Transaction unbalanced: Debits: " + totalDebits + ", Credits: " + totalCredits);
        }
    }

    public UUID getId(){ 
        return id; 
    }
    
    public LocalDate getDate(){ 
        return date; 
    }
    
    public String getDescription(){ 
        return description; 
    }
    
    public List<JournalEntryItem> getItems(){ 
        return items; 
    }
}
