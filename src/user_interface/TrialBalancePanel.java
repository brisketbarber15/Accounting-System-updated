package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import statics.UnadjustedTrialBalance;

/** A trial balance table: the opening one or the unadjusted one. */
class TrialBalancePanel extends Page {
    private final boolean opening;
    private final DefaultTableModel model = Ui.readOnlyModel("Code", "Account Title", "Debit", "Credit");
    private final JLabel status = Ui.note(" ");

    TrialBalancePanel(boolean opening){
        this.opening = opening;
        javax.swing.JTable table = Ui.table(model, true, 2, 3);
        Ui.width(table, 0, 70);
        Ui.width(table, 2, 150);
        Ui.width(table, 3, 150);
        add(Ui.scroll(table, null), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        refresh();
    }

    @Override
    void refresh(){
        UnadjustedTrialBalance tb = opening ? Books.get().openingTrialBalance() : Books.get().unadjustedTrialBalance();
        model.setRowCount(0);
        for(UnadjustedTrialBalance.Row row : tb.getRows()){
            model.addRow(new Object[]{row.account().code(), row.account().name(), Ui.money(row.debit()), Ui.money(row.credit())});
        }
        model.addRow(new Object[]{"", "Total", Ui.money(tb.getTotalDebits()), Ui.money(tb.getTotalCredits())});
        if(tb.isBalanced()){
            status.setText("Debits equal credits.");
        }else{
            status.setText("Debits and credits differ by " + Ui.money(tb.getTotalDebits().subtract(tb.getTotalCredits()).abs()) + ".");
        }
    }
}
