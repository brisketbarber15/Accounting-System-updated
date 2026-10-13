package user_interface;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class AboutPanel extends Page {
    private static final String TEXT = """
            Accounting System

            A double-entry accounting program for a small merchandising business.

            What it does
              - Records transactions in the General Journal; unbalanced transactions are not posted.
              - Posts them to the General Ledger (T-accounts) starting from the Begining Trial Balance.
              - Tracks credit sales and purchases under 2/10, n/30 terms: a 2% discount when settled
                within 10 days of the invoice date, the full amount due within 30 days.
              - Prepares the Unadjusted Trial Balance and the financial statements:
                Statement of Financial Performance, Statement of Changes in Equity and
                Statement of Financial Position.

            Notes
              - Inventory is periodic: cost of goods sold = beginning inventory + net purchases - ending inventory.
              - Transactions are kept in memory while the program is open.
            """;

    AboutPanel(){
        JTextArea area = new JTextArea(TEXT);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setFont(area.getFont().deriveFont(16f));
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    @Override
    void refresh(){
    }
}
