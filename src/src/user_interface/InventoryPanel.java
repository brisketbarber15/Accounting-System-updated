package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import statics.FinancialStatements;

class InventoryPanel extends Page {
    private final StatementTable schedule = new StatementTable();
    private final JTextField countField = new JTextField(12);
    private final JLabel info = Ui.note(" ");

    InventoryPanel(){
        add(Ui.note("Cost of goods sold schedule (periodic inventory)"), BorderLayout.NORTH);
        add(schedule, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton apply = new JButton("Apply Count");
        JButton useLedger = new JButton("Use Ledger Balance");
        apply.addActionListener(e -> applyCount());
        useLedger.addActionListener(e -> {
            Books.get().setEndingInventory(null);
            refresh();
        });
        controls.add(new JLabel("Ending inventory count:"));
        controls.add(countField);
        controls.add(apply);
        controls.add(useLedger);
        south.add(controls, BorderLayout.NORTH);
        south.add(info, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
        refresh();
    }

    private void applyCount(){
        try{
            BigDecimal count = new BigDecimal(countField.getText().replace(",", "").trim()).setScale(2, RoundingMode.HALF_UP);
            if(count.signum() < 0){
                throw new NumberFormatException();
            }
            Books.get().setEndingInventory(count);
            refresh();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Enter the ending inventory as a number of 0 or more.", "Inventory", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    void refresh(){
        FinancialStatements fs = Books.get().statements();
        schedule.setLines(fs.costOfGoodsSoldSchedule());
        BigDecimal counted = Books.get().getEndingInventory();
        countField.setText(counted == null ? "" : counted.toPlainString());
        info.setText(counted == null
                ? "Using the Merchandise Inventory ledger balance. Enter a count to use it in the statements instead."
                : "Using a counted ending inventory of " + Ui.money(counted) + " in the statements.");
    }
}
