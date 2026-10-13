package user_interface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import system.Account;
import system.JournalEntryItem;
import system.TAccount;

/** One T-account: debits on the left, credits on the right, and the balance underneath. */
final class TAccountView extends JPanel {

    TAccountView(TAccount taccount){
        super(new BorderLayout());
        Account account = taccount.getAccount();
        setBorder(BorderFactory.createTitledBorder(account.code() + " " + account.name() + (account.isContra() ? " (contra)" : "")));

        List<JournalEntryItem> debits = taccount.getDebits();
        List<JournalEntryItem> credits = taccount.getCredits();
        DefaultTableModel model = Ui.readOnlyModel("Debit", "Credit");
        int rows = Math.max(debits.size(), credits.size());
        for(int i = 0; i < rows; i++){
            model.addRow(new Object[]{
                i < debits.size() ? Ui.money(debits.get(i).amount()) : "",
                i < credits.size() ? Ui.money(credits.get(i).amount()) : ""});
        }
        JTable table = Ui.table(model, false, 0, 1);
        add(table.getTableHeader(), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);

        String side = account.getNormalBalance().name().toLowerCase();
        JLabel balance = new JLabel("Balance (" + side + " normal): " + Ui.money(taccount.getNetBalance()));
        balance.setFont(balance.getFont().deriveFont(Font.BOLD));
        balance.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));
        add(balance, BorderLayout.SOUTH);

        setAlignmentX(LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
    }
}
