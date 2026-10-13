/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package statics;

/**
 *
 * @author Ryan
 */
import system.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.HashMap;

public class OpeningTrialBalance {
    int[] codes = {
        100, 101, 110, 111, 112, 113, 120, 130, 140, 141, 
        150, 151, 152, 153, 154, 155, 160, 161, 200, 201, 
        202, 300, 301, 400, 500, 501, 600, 602, 605, 610, 
        640
    };

    double[] amounts = {
        284368.92, 16000.00, 240000.00, 2400.00, 313600.00, 9408.00, 975744.00, 14500.00, 12750.00, 150000.00, 
        650000.00, 74250.00, 735000.00, 134500.00, 410000.00, 33000.00, 150000.00, 18500.00, 500000.00, 120710.67, 
        432320.00, 2555395.05, 40500.00, 846720.00, 533244.00, 4800.00, 12000.00, 45700.00, 18545.40, 75000.00, 
        3651.40
    }; 
    
    Map<Account, BigDecimal> openingBalances = new HashMap<>();
    
    public Transaction createOpeningBalanceTransaction(LocalDate periodStartDate){ // synthetic initialization of opening balances in transaction
        List<JournalEntryItem> items = new ArrayList<>();
        ChartofAccounts coa = new ChartofAccounts();
        
        for(int i=0; i<codes.length; i++){
            final int index = i; 
            openingBalances.put(coa.getAccounts().stream()
                .filter(acc -> acc.code() == codes[index])
                .findFirst()
                .orElse(null), 
                BigDecimal.valueOf(amounts[index]));
        }

        openingBalances.forEach((account, amount) -> {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }

            NormalBalance normal = account.getNormalBalance();
            EntryType entryType;

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                entryType = (normal == NormalBalance.DEBIT) ? EntryType.DEBIT : EntryType.CREDIT;
            } else {
                entryType = (normal == NormalBalance.DEBIT) ? EntryType.CREDIT : EntryType.DEBIT;
            }

            items.add(new JournalEntryItem(account, amount.abs(), entryType));
        });

        return new Transaction(
            UUID.randomUUID(),
            periodStartDate,
            "Opening Trial Balance Initialization",
            items
        );
    }
}
