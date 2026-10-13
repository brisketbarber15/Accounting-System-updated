package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import statics.FinancialStatements;
import statics.GeneralLedger;
import statics.UnadjustedTrialBalance;
import system.EntryType;
import system.JournalEntryItem;
import system.Transaction;

class DashBoardPanel extends Page {
    private final JLabel cash = valueLabel();
    private final JLabel receivable = valueLabel();
    private final JLabel assets = valueLabel();
    private final JLabel liabilities = valueLabel();
    private final JLabel equity = valueLabel();
    private final JLabel income = valueLabel();
    private final JLabel count = valueLabel();
    private final JLabel status = valueLabel();
    private final DefaultTableModel recent = Ui.readOnlyModel("Date", "Description", "Amount");

    DashBoardPanel(){
        JPanel cards = new JPanel(new GridLayout(2, 4, 10, 10));
        cards.add(card("Cash", cash));
        cards.add(card("Receivables (net)", receivable));
        cards.add(card("Total Assets", assets));
        cards.add(card("Total Liabilities", liabilities));
        cards.add(card("Owner's Equity", equity));
        cards.add(card("Net Income", income));
        cards.add(card("Transactions Posted", count));
        cards.add(card("Trial Balance", status));
        add(cards, BorderLayout.NORTH);
        javax.swing.JTable recentTable = Ui.table(recent, false, 2);
        Ui.width(recentTable, 0, 110);
        Ui.width(recentTable, 2, 150);
        add(Ui.scroll(recentTable, "Recent Transactions"), BorderLayout.CENTER);
        refresh();
    }

    private static JLabel valueLabel(){
        JLabel label = new JLabel("-");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        return label;
    }

    private static JPanel card(String title, JLabel value){
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        panel.add(new JLabel(title), BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    @Override
    void refresh(){
        Books books = Books.get();
        GeneralLedger ledger = books.ledger();
        FinancialStatements fs = books.statements();
        UnadjustedTrialBalance tb = books.unadjustedTrialBalance();

        cash.setText(Ui.money(ledger.balance(Books.CASH)));
        receivable.setText(Ui.money(ledger.balance(Books.ACCOUNTS_RECEIVABLE).subtract(ledger.balance(113))));
        assets.setText(Ui.money(fs.getTotalAssets()));
        liabilities.setText(Ui.money(fs.getTotalLiabilities()));
        equity.setText(Ui.money(fs.getEndingCapital()));
        income.setText(Ui.money(fs.getNetIncome()));
        count.setText(String.valueOf(books.getTransactions().size()));
        status.setText(tb.isBalanced() ? "Balanced" : "Out of balance");

        recent.setRowCount(0);
        List<Transaction> all = books.getTransactions();
        for(int i = all.size() - 1; i >= 0 && i >= all.size() - 10; i--){
            Transaction t = all.get(i);
            BigDecimal total = BigDecimal.ZERO;
            for(JournalEntryItem item : t.getItems()){
                if(item.entryType() == EntryType.DEBIT){
                    total = total.add(item.amount());
                }
            }
            recent.addRow(new Object[]{t.getDate().toString(), t.getDescription(), Ui.money(total)});
        }
    }
}
