/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package accounting;

/**
 *
 * @author Ryan
 */
import functions.*;
import system.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class Accounting {

    public static void main(String[] args) {
        /** Accounting system
         *  
         * input: date, account titles, debit and credit amounts
         * perpetual inventory system
         * credit terms & discounts must be tracked to apply 2/10 n/30
         * input validation for double entry, no unbalance entry 
         * 
         * general journal
         * general ledger
         * 
         * output: unadjusted trial balance, and statements of financial performance, financial position, and changes in equity
         * 
         * TO-DO list: 
         * ===========================================================================================================
         *  User interface - wire framing and JFrame
            Perpetual inventory system - sales must reduce inventory *******************************************************************
            Credit term tracking - 2/10 n/30 *******************************************************************************************
            Chart of accounts and opening trial balance can be pulled from a csv file or a database
            Database for transactions that can be exported to csv file
            Unadjusted trial balance and:
            1. Statement of Financial Performance - Income statement
            2. Statement of Financial Position - Balance sheet
            3. Statement of Changes in Equity 
            date for transaction is set to the date of input, must be changed to user input date
            every time user use an account, the system search the chart of account, idk if there is an efficient way
            display methods in utils will be removed once the jframe is ready
            aggregate transactions to GeneralJournal and TAccounts to GeneralLedger in statics ****************************************
            jframe level input validation for account name and account code 
            jframe level input validation for credits - debits balance (logic is in Transaction.java-- currently, unbalance transaction are noticed but still goes trough)
            LocalDate.now() instance should be change to the actual user input date
            
            
            handle nulls in transaction isnt balance
            handle nulls if entry isnt found or amount is greater than in credit term tracking
         * ============================================================================================================
         */

        Transaction t0 = Utils.initializeOpeningTrialBalance(LocalDate.now());

        Account cash = Utils.findAccount("cash");
        Account accountsPayable = Utils.findAccount("accounts payable");

        // ---- Transaction 1: Loan 1000 pesos ----
        JournalEntryItem entry1 = Utils.createEntry(cash, 1000, EntryType.DEBIT);
        JournalEntryItem entry2 = Utils.createEntry(accountsPayable, 1000, EntryType.CREDIT);
        Transaction t1 = Utils.createTransaction(
                LocalDate.now(),
                "Loan 1000 pesos",
                new JournalEntryItem[] {entry1, entry2}
        );

        // ---- Transaction 2: Paid 500 pesos ----
        JournalEntryItem entry3 = Utils.createEntry(accountsPayable, 500, EntryType.DEBIT);
        JournalEntryItem entry4 = Utils.createEntry(cash, 500, EntryType.CREDIT);
        Transaction t2 = Utils.createTransaction(
                LocalDate.now(),
                "Paid 500 pesos",
                new JournalEntryItem[] {entry3, entry4}
        );

        // ---- Collect valid transactions ----
        List<Transaction> validTransactions = new ArrayList<>();
        if(t1 != null) validTransactions.add(t1);
        if(t2 != null) validTransactions.add(t2);

        // ================= PERPETUAL INVENTORY DEMO =================
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("P001", "Widget", 
                new BigDecimal("600"), new BigDecimal("1000"), 0));
        inventory.addProduct(new Product("P002", "Gadget", 
                new BigDecimal("300"), new BigDecimal("500"), 0));

        Transaction purchase = Utils.createPurchaseTransaction(
                LocalDate.now(), inventory, "P001", 10, 
                new BigDecimal("600"), accountsPayable);
        if(purchase != null) validTransactions.add(purchase);

        Transaction purchase2 = Utils.createPurchaseTransaction(
                LocalDate.now(), inventory, "P002", 5, 
                new BigDecimal("300"), accountsPayable);
        if(purchase2 != null) validTransactions.add(purchase2);

        Transaction sale = Utils.createSaleTransaction(
                LocalDate.now(), inventory, "P001", 3, 
                new BigDecimal("1000"), cash);
        if(sale != null) validTransactions.add(sale);

        inventory.display();

        // 
        for(Transaction t : validTransactions){
            Utils.displayTransaction(t);
        }

        // ================= TACCOUNTS =================
        TAccount cashTAccount = Utils.createTAccountAndInitializeBeginingBalance("cash", t0);
        Utils.addEntries(cashTAccount, validTransactions);
        Utils.displayTAcount(cashTAccount);

        TAccount APTAccount = Utils.createTAccountAndInitializeBeginingBalance("accounts payable", t0);
        Utils.addEntries(APTAccount, validTransactions);
        Utils.displayTAcount(APTAccount);
    }
}