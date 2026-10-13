package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import system.CreditTermTrack;

class CreditTrackingPanel extends Page {
    private static final String[] COLUMNS = {"Invoice Date", "Open Balance", "Discount Until", "Due Date", "Status"};
    private final DefaultTableModel receivables = Ui.readOnlyModel(COLUMNS);
    private final DefaultTableModel payables = Ui.readOnlyModel(COLUMNS);

    CreditTrackingPanel(){
        add(Ui.note("Credit terms 2/10, n/30: 2% discount if settled within 10 days of the invoice; full amount due in 30 days."), BorderLayout.NORTH);
        JPanel tables = new JPanel(new GridLayout(2, 1, 8, 8));
        javax.swing.JTable receivableTable = Ui.table(receivables, false, 1);
        javax.swing.JTable payableTable = Ui.table(payables, false, 1);
        for(javax.swing.JTable t : new javax.swing.JTable[]{receivableTable, payableTable}){
            Ui.width(t, 0, 110);
            Ui.width(t, 1, 140);
            Ui.width(t, 2, 120);
            Ui.width(t, 3, 110);
        }
        tables.add(Ui.scroll(receivableTable, "Open Accounts Receivable (customers owe us)"));
        tables.add(Ui.scroll(payableTable, "Open Accounts Payable (we owe suppliers)"));
        add(tables, BorderLayout.CENTER);
        refresh();
    }

    private static void fill(DefaultTableModel model, Map<LocalDate, BigDecimal> open){
        model.setRowCount(0);
        LocalDate today = LocalDate.now();
        for(Map.Entry<LocalDate, BigDecimal> entry : open.entrySet()){
            LocalDate invoice = entry.getKey();
            long age = ChronoUnit.DAYS.between(invoice, today);
            String status;
            if(age < 0){
                status = "Dated in the future";
            }else if(age <= CreditTermTrack.DISCOUNT_DAYS){
                status = "2% discount available (" + (CreditTermTrack.DISCOUNT_DAYS - age) + " days left)";
            }else if(age <= CreditTermTrack.CREDIT_DAYS){
                status = "Net amount due in " + (CreditTermTrack.CREDIT_DAYS - age) + " days";
            }else{
                status = "Overdue by " + (age - CreditTermTrack.CREDIT_DAYS) + " days";
            }
            model.addRow(new Object[]{
                invoice.toString(),
                Ui.money(entry.getValue()),
                invoice.plusDays(CreditTermTrack.DISCOUNT_DAYS).toString(),
                invoice.plusDays(CreditTermTrack.CREDIT_DAYS).toString(),
                status});
        }
    }

    @Override
    void refresh(){
        CreditTermTrack credits = Books.get().getCredits();
        fill(receivables, credits.getAccountReceivables());
        fill(payables, credits.getAccountPayables());
    }
}
