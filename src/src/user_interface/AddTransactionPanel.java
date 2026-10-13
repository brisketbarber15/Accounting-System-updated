package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import system.Account;
import system.EntryType;

class AddTransactionPanel extends Page {
    private static final int ACCOUNT = 0, SIDE = 1, AMOUNT = 2, INVOICE = 3;

    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField descriptionField = new JTextField(30);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Account Title", "Debit / Credit", "Amount", "Invoice Date"}, 0);
    private final JTable table = new JTable(model);
    private final JLabel summary = new JLabel(" ");

    private record Parsed(String error, List<Books.Line> lines) {}

    AddTransactionPanel(){
        add(buildForm(), BorderLayout.NORTH);
        buildTable();
        add(new javax.swing.JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        resetLines();
        model.addTableModelListener(e -> updateSummary());
        dateField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ updateSummary(); }
            public void removeUpdate(DocumentEvent e){ updateSummary(); }
            public void changedUpdate(DocumentEvent e){ updateSummary(); }
        });
        updateSummary();
    }

    private JPanel buildForm(){
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Date (yyyy-MM-dd)"), c);
        c.gridx = 1;
        form.add(dateField, c);
        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Description"), c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        form.add(descriptionField, c);
        return form;
    }

    private void buildTable(){
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(java.awt.Color.GRAY);
        table.getTableHeader().setReorderingAllowed(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JComboBox<String> accountBox = new JComboBox<>();
        accountBox.addItem("");
        for(Account a : Books.get().getAccounts()){
            accountBox.addItem(a.code() + " " + a.name());
        }
        table.getColumnModel().getColumn(ACCOUNT).setCellEditor(new DefaultCellEditor(accountBox));
        table.getColumnModel().getColumn(SIDE).setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Debit", "Credit"})));

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(AMOUNT).setCellRenderer(right);
        Ui.width(table, SIDE, 120);
        Ui.width(table, AMOUNT, 150);
        Ui.width(table, INVOICE, 150);
    }

    private JPanel buildBottom(){
        JPanel bottom = new JPanel(new BorderLayout(0, 4));
        summary.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        summary.setFont(summary.getFont().deriveFont(java.awt.Font.BOLD));
        JPanel hint = new JPanel();
        hint.setLayout(new javax.swing.BoxLayout(hint, javax.swing.BoxLayout.Y_AXIS));
        hint.add(Ui.note("Invoice Date: needed when collecting a receivable or paying a payable."));
        hint.add(Ui.note("Settled within 10 days? The 2% discount is added automatically; enter the cash at 98%."));
        hint.add(Ui.note("Leave Invoice Date blank for balances from before this system."));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addLine = new JButton("Add Line");
        JButton removeLine = new JButton("Remove Selected Line");
        JButton clear = new JButton("Clear");
        JButton post = new JButton("Post Transaction");
        JButton undo = new JButton("Undo Last Transaction");
        addLine.addActionListener(e -> model.addRow(new Object[]{"", "Debit", "", ""}));
        removeLine.addActionListener(e -> removeSelectedLine());
        clear.addActionListener(e -> resetForm());
        post.addActionListener(e -> postTransaction());
        undo.addActionListener(e -> undoLast());
        buttons.add(addLine);
        buttons.add(removeLine);
        buttons.add(clear);
        buttons.add(post);
        buttons.add(undo);

        JPanel top = new JPanel(new BorderLayout());
        top.add(summary, BorderLayout.NORTH);
        top.add(hint, BorderLayout.CENTER);
        bottom.add(top, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);
        return bottom;
    }

    private void resetLines(){
        model.setRowCount(0);
        model.addRow(new Object[]{"", "Debit", "", ""});
        model.addRow(new Object[]{"", "Credit", "", ""});
    }

    private void resetForm(){
        if(table.isEditing()){
            table.getCellEditor().cancelCellEditing();
        }
        descriptionField.setText("");
        resetLines();
    }

    private void removeSelectedLine(){
        int row = table.getSelectedRow();
        if(row < 0){
            JOptionPane.showMessageDialog(this, "Select a line to remove first.");
            return;
        }
        if(model.getRowCount() <= 2){
            JOptionPane.showMessageDialog(this, "A transaction needs at least two lines.");
            return;
        }
        if(table.isEditing()){
            table.getCellEditor().cancelCellEditing();
        }
        model.removeRow(row);
    }

    private String text(int row, int column){
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private Parsed parse(){
        List<Books.Line> out = new ArrayList<>();
        for(int r = 0; r < model.getRowCount(); r++){
            String n = "Line " + (r + 1) + ": ";
            String accountText = text(r, ACCOUNT);
            if(accountText.isEmpty()){
                return new Parsed(n + "choose an account.", null);
            }
            Account account = Books.get().findByCode(Integer.parseInt(accountText.split(" ")[0]));
            EntryType type = "Credit".equals(text(r, SIDE)) ? EntryType.CREDIT : EntryType.DEBIT;

            BigDecimal amount;
            try{
                amount = new BigDecimal(text(r, AMOUNT).replace(",", "")).setScale(2, RoundingMode.HALF_UP);
            }catch(NumberFormatException ex){
                return new Parsed(n + "enter the amount as a number.", null);
            }
            if(amount.signum() <= 0){
                return new Parsed(n + "the amount must be greater than zero.", null);
            }

            boolean needsInvoice = (account.code() == Books.ACCOUNTS_RECEIVABLE && type == EntryType.CREDIT)
                    || (account.code() == Books.ACCOUNTS_PAYABLE && type == EntryType.DEBIT);
            LocalDate invoice = null;
            if(needsInvoice && !text(r, INVOICE).isEmpty()){
                try{
                    invoice = LocalDate.parse(text(r, INVOICE));
                }catch(DateTimeParseException ex){
                    return new Parsed(n + "enter the invoice date as yyyy-MM-dd.", null);
                }
            }
            out.add(new Books.Line(account, amount, type, invoice));
        }
        return new Parsed(null, out);
    }

    private LocalDate parseDate(){
        try{
            return LocalDate.parse(dateField.getText().trim());
        }catch(DateTimeParseException ex){
            return null;
        }
    }

    private void updateSummary(){
        Parsed parsed = parse();
        if(parsed.error() != null){
            summary.setText(parsed.error());
            return;
        }
        LocalDate date = parseDate();
        if(date == null){
            summary.setText("Enter the date as yyyy-MM-dd.");
            return;
        }
        Books.Preview preview = Books.get().preview(date, parsed.lines());
        if(preview.error() != null){
            summary.setText(preview.error());
            return;
        }
        String text = "Debits " + Ui.money(preview.debits()) + "   |   Credits " + Ui.money(preview.credits()) + "   |   "
                + (preview.isBalanced() ? "Balanced" : "Out of balance by " + Ui.money(preview.debits().subtract(preview.credits()).abs()));
        if(preview.discount().signum() > 0){
            text += "   |   Includes 2% prompt-payment discount of " + Ui.money(preview.discount());
        }
        summary.setText(text);
    }

    private void postTransaction(){
        if(table.isEditing()){
            table.getCellEditor().stopCellEditing();
        }
        LocalDate date = parseDate();
        if(date == null){
            error("Enter the transaction date as yyyy-MM-dd.");
            return;
        }
        Parsed parsed = parse();
        if(parsed.error() != null){
            error(parsed.error());
            return;
        }
        String problem = Books.get().post(date, descriptionField.getText(), parsed.lines());
        if(problem != null){
            error(problem);
            return;
        }
        JOptionPane.showMessageDialog(this, "Transaction posted.", "Add Transaction", JOptionPane.INFORMATION_MESSAGE);
        resetForm();
    }

    private void undoLast(){
        if(Books.get().getTransactions().isEmpty()){
            JOptionPane.showMessageDialog(this, "There is no posted transaction to undo.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Remove the last posted transaction?", "Undo", JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION){
            Books.get().undoLast();
            updateSummary();
        }
    }

    private void error(String message){
        JOptionPane.showMessageDialog(this, message, "Cannot post", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    void refresh(){
        updateSummary();
    }
}
