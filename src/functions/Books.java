package functions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import statics.ChartofAccounts;
import statics.FinancialStatements;
import statics.GeneralJournal;
import statics.GeneralLedger;
import statics.OpeningTrialBalance;
import statics.UnadjustedTrialBalance;
import system.Account;
import system.CreditTermTrack;
import system.EntryType;
import system.JournalEntryItem;
import system.Transaction;

/**
 * The company's books for the user interface: chart of accounts, opening balances, the general
 * journal, credit term tracking and the statements built from them.
 * Unlike Utils.createTransaction, nothing here opens dialogs: errors are returned as messages
 * so the screens decide how to show them, and an unbalanced transaction is never posted.
 */
public final class Books {
    public static final int CASH = 100;
    public static final int ACCOUNTS_RECEIVABLE = 112;
    public static final int MERCHANDISE_INVENTORY = 120;
    public static final int ACCOUNTS_PAYABLE = 202;
    public static final int OWNERS_CAPITAL = 300;
    public static final int SALES_DISCOUNTS = 401;
    public static final int PURCHASES_DISCOUNTS = 502;

    private static final Books INSTANCE = new Books();

    public static Books get(){
        return INSTANCE;
    }

    /** One line typed on the Add Transaction screen. invoiceDate is only used when collecting a receivable or paying a payable. */
    public record Line(Account account, BigDecimal amount, EntryType type, LocalDate invoiceDate) {}

    /** What a transaction would look like once the credit-term discount is added. */
    public record Preview(String error, List<JournalEntryItem> items, BigDecimal debits, BigDecimal credits, BigDecimal discount, CreditTermTrack tracker) {
        public boolean isBalanced(){
            return error == null && debits.compareTo(credits) == 0;
        }
    }

    private final ChartofAccounts coa = new ChartofAccounts();
    private final Transaction opening = new OpeningTrialBalance().createOpeningBalanceTransaction(LocalDate.now());
    private final GeneralLedger openingLedger = new GeneralLedger(coa.getAccounts(), opening, List.of());
    private final GeneralJournal journal = new GeneralJournal();
    private final Deque<CreditTermTrack> creditHistory = new ArrayDeque<>();
    private CreditTermTrack credits = new CreditTermTrack();
    private BigDecimal endingInventory; // counted ending inventory; null means use the ledger balance

    private Books(){
    }

    public List<Account> getAccounts(){
        return coa.getAccounts();
    }

    public Account findByCode(int code){
        return coa.getAccounts().stream().filter(a -> a.code() == code).findFirst().orElse(null);
    }

    public Transaction getOpeningTransaction(){
        return opening;
    }

    public List<Transaction> getTransactions(){
        return journal.getTransactions();
    }

    public CreditTermTrack getCredits(){
        return credits;
    }

    public BigDecimal getEndingInventory(){
        return endingInventory;
    }

    public void setEndingInventory(BigDecimal count){
        this.endingInventory = count;
    }

    public GeneralLedger ledger(){
        return new GeneralLedger(coa.getAccounts(), opening, journal.getTransactions());
    }

    public UnadjustedTrialBalance openingTrialBalance(){
        return new UnadjustedTrialBalance(openingLedger);
    }

    public UnadjustedTrialBalance unadjustedTrialBalance(){
        return new UnadjustedTrialBalance(ledger());
    }

    public FinancialStatements statements(){
        return new FinancialStatements(ledger(), openingLedger.balance(MERCHANDISE_INVENTORY), openingLedger.balance(OWNERS_CAPITAL), endingInventory);
    }

    /** Works out the final entries (including any 2/10, n/30 discount) without changing anything. */
    public Preview preview(LocalDate date, List<Line> lines){
        List<JournalEntryItem> items = new ArrayList<>();
        CreditTermTrack work = credits.copy();
        BigDecimal discount = BigDecimal.ZERO;

        for(Line line : lines){
            if(line.account() == null){
                return failed("Choose an account for every line.");
            }
            if(line.amount() == null || line.amount().signum() <= 0){
                return failed("Every amount must be greater than zero.");
            }
            items.add(new JournalEntryItem(line.account(), line.amount(), line.type()));

            int code = line.account().code();
            if(code == ACCOUNTS_RECEIVABLE){
                if(line.type() == EntryType.DEBIT){
                    work.addAccountReceivable(date, line.amount());
                }else if(line.invoiceDate() != null){ // blank invoice date: balance from before this system
                    String error = invoiceError("receivable", line.invoiceDate(), date, work.receivableBalance(line.invoiceDate()), line.amount());
                    if(error != null){
                        return failed(error);
                    }
                    work.settleReceivable(line.invoiceDate(), line.amount());
                    if(CreditTermTrack.qualifiesForDiscount(line.invoiceDate(), date)){
                        BigDecimal given = CreditTermTrack.discountOn(line.amount());
                        if(given.signum() > 0){
                            items.add(new JournalEntryItem(findByCode(SALES_DISCOUNTS), given, EntryType.DEBIT));
                            discount = discount.add(given);
                        }
                    }
                }
            }else if(code == ACCOUNTS_PAYABLE){
                if(line.type() == EntryType.CREDIT){
                    work.addAccountPayable(date, line.amount());
                }else if(line.invoiceDate() != null){
                    String error = invoiceError("payable", line.invoiceDate(), date, work.payableBalance(line.invoiceDate()), line.amount());
                    if(error != null){
                        return failed(error);
                    }
                    work.settlePayable(line.invoiceDate(), line.amount());
                    if(CreditTermTrack.qualifiesForDiscount(line.invoiceDate(), date)){
                        BigDecimal received = CreditTermTrack.discountOn(line.amount());
                        if(received.signum() > 0){
                            items.add(new JournalEntryItem(findByCode(PURCHASES_DISCOUNTS), received, EntryType.CREDIT));
                            discount = discount.add(received);
                        }
                    }
                }
            }
        }

        BigDecimal debits = BigDecimal.ZERO;
        BigDecimal creditTotal = BigDecimal.ZERO;
        for(JournalEntryItem item : items){
            if(item.entryType() == EntryType.DEBIT){
                debits = debits.add(item.amount());
            }else{
                creditTotal = creditTotal.add(item.amount());
            }
        }
        return new Preview(null, items, debits, creditTotal, discount, work);
    }

    private static String invoiceError(String kind, LocalDate invoiceDate, LocalDate date, BigDecimal open, BigDecimal amount){
        if(date.isBefore(invoiceDate)){
            return "The transaction date is before the invoice date " + invoiceDate + ".";
        }
        if(open.signum() <= 0){
            return "There is no open accounts " + kind + " dated " + invoiceDate + ".";
        }
        if(amount.compareTo(open) > 0){
            return "The amount is more than the open accounts " + kind + " of " + open.toPlainString() + " dated " + invoiceDate + ".";
        }
        return null;
    }

    private static Preview failed(String message){
        return new Preview(message, List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
    }

    /** Posts a transaction. Returns null on success, otherwise the reason it was not posted. */
    public String post(LocalDate date, String description, List<Line> lines){
        if(date == null){
            return "Enter a valid transaction date (yyyy-MM-dd).";
        }
        if(description == null || description.isBlank()){
            return "Enter a description for the transaction.";
        }
        if(lines == null || lines.size() < 2){
            return "A transaction needs at least two lines.";
        }
        Preview preview = preview(date, lines);
        if(preview.error() != null){
            return preview.error();
        }
        if(!preview.isBalanced()){
            return "Transaction unbalanced: Debits: " + preview.debits().toPlainString() + ", Credits: " + preview.credits().toPlainString();
        }
        creditHistory.push(credits);
        credits = preview.tracker();
        journal.add(new Transaction(UUID.randomUUID(), date, description.trim(), preview.items()));
        return null;
    }

    public boolean undoLast(){
        if(!journal.removeLast()){
            return false;
        }
        credits = creditHistory.pop();
        return true;
    }

    public void clearPosted(){
        journal.clear();
        creditHistory.clear();
        credits = new CreditTermTrack();
        endingInventory = null;
    }
}
