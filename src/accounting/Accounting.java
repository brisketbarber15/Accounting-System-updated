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

public class Accounting {

    /**
     * @param args the command line arguments
     */
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
        
        
        Transaction t0 = Utils.initializeOpeningTrialBalance(LocalDate.now()); // impute begining balances to ledger
        
        
        Account cash = Utils.findAccount("cash"); // create account object - to-do validation if null
        Account accountsPayable = Utils.findAccount("accounts payable");
        
        
        JournalEntryItem entry1 = Utils.createEntry(cash, 1000, EntryType.DEBIT); // creating entry
        JournalEntryItem entry2 = Utils.createEntry(accountsPayable, 1000, EntryType.CREDIT);
        Transaction t1 = Utils.createTransaction(LocalDate.now(), "Loan 1000 pesos", new JournalEntryItem[] {entry1, entry2}); // aggregating entries to one transaction
        
        
        JournalEntryItem entry3 = Utils.createEntry(accountsPayable, 500, EntryType.DEBIT); // second transaction
        JournalEntryItem entry4 = Utils.createEntry(cash, 500, EntryType.CREDIT);
        Transaction t2 = Utils.createTransaction(LocalDate.now(), "Paid 500 pesos", new JournalEntryItem[] {entry3, entry4}); // LocalDate.now() should be change to the actual user input date
        
        
        Utils.displayTransaction(t1); // printing for testing
        Utils.displayTransaction(t2);
        
        
        
        
        TAccount cashTAccount = Utils.createTAccountAndInitializeBeginingBalance("cash", t0); // create a TAccount and initializes its begining balannce
        Utils.addEntries(cashTAccount, List.of(t1, t2)); // add amounts to the pased taccount
        Utils.displayTAcount(cashTAccount); // display taccount
        
        TAccount APTAccount = Utils.createTAccountAndInitializeBeginingBalance("accounts payable", t0); 
        Utils.addEntries(APTAccount, List.of(t1, t2)); 
        Utils.displayTAcount(APTAccount);
    }
}
