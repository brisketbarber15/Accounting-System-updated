package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import statics.FinancialStatements;

class StatementsPanel extends Page {
    private final StatementTable income = new StatementTable();
    private final StatementTable equity = new StatementTable();
    private final StatementTable position = new StatementTable();
    private final JLabel status = Ui.note(" ");

    StatementsPanel(){
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Statement of Financial Performance", income);
        tabs.addTab("Statement of Changes in Equity", equity);
        tabs.addTab("Statement of Financial Position", position);
        add(tabs, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        refresh();
    }

    @Override
    void refresh(){
        FinancialStatements fs = Books.get().statements();
        income.setLines(fs.incomeStatement());
        equity.setLines(fs.changesInEquity());
        position.setLines(fs.balanceSheet());
        status.setText((fs.isBalanced() ? "Total assets equal liabilities plus owner\u2019s equity." : "Total assets do not equal liabilities plus owner\u2019s equity.")
                + "  Built from the unadjusted trial balance (no adjusting entries yet).");
    }
}
