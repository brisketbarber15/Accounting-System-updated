package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import system.EntryType;
import system.JournalEntryItem;
import system.Transaction;

class GeneralJournalPanel extends Page {
    private final DefaultTableModel model = Ui.readOnlyModel("Date", "Account Title and Description", "Debit", "Credit");
    private final JTable table = Ui.table(model, false, 2, 3);
    private final JLabel empty = Ui.note(" ");

    GeneralJournalPanel(){
        // description rows (no date, no amounts) are shown in italics
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int column){
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                setHorizontalAlignment(column >= 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                boolean description = model.getValueAt(row, 0).toString().isEmpty()
                        && model.getValueAt(row, 2).toString().isEmpty() && model.getValueAt(row, 3).toString().isEmpty();
                c.setFont(t.getFont().deriveFont(description ? Font.ITALIC : Font.PLAIN));
                return c;
            }
        };
        for(int i = 0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        Ui.width(table, 0, 110);
        Ui.width(table, 2, 150);
        Ui.width(table, 3, 150);
        add(Ui.scroll(table, null), BorderLayout.CENTER);
        add(empty, BorderLayout.SOUTH);
        refresh();
    }

    @Override
    void refresh(){
        model.setRowCount(0);
        for(Transaction t : Books.get().getTransactions()){
            boolean first = true;
            // debits first, then credits, as in a journal
            java.util.List<JournalEntryItem> ordered = new java.util.ArrayList<>();
            for(JournalEntryItem item : t.getItems()){
                if(item.entryType() == EntryType.DEBIT){
                    ordered.add(item);
                }
            }
            for(JournalEntryItem item : t.getItems()){
                if(item.entryType() == EntryType.CREDIT){
                    ordered.add(item);
                }
            }
            for(JournalEntryItem item : ordered){
                boolean debit = item.entryType() == EntryType.DEBIT;
                model.addRow(new Object[]{
                    first ? t.getDate().toString() : "",
                    (debit ? "" : "      ") + item.account().name(),
                    debit ? Ui.money(item.amount()) : "",
                    debit ? "" : Ui.money(item.amount())});
                first = false;
            }
            model.addRow(new Object[]{"", "(" + t.getDescription() + ")", "", ""});
        }
        empty.setText(model.getRowCount() == 0
                ? "No transactions yet. Post one from Add Transaction. The opening balances are on Begining Trial Balance."
                : "Opening balances are not repeated here; see Begining Trial Balance.");
    }
}
