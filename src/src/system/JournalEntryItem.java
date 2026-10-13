/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system;

/**
 *
 * @author Ryan
 */
import java.math.BigDecimal;
public record JournalEntryItem(Account account, BigDecimal amount, EntryType entryType) {
    public JournalEntryItem {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive"); // change to joptionpane
        }
    }
}
