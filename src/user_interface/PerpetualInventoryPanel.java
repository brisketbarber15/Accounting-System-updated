/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user_interface;

/**
 *
 * @author Brent
 */
import functions.Books;
import functions.PerpetualInventory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import system.Product;

class PerpetualInventoryPanel extends Page {

    private static final int ID = 0, NAME = 1, COST = 2, PRICE = 3, QTY = 4, VALUE = 5;

    private final DefaultTableModel model = Ui.readOnlyModel(
            "ID", "Name", "Avg Cost", "Selling Price", "Qty on Hand", "Total Value");
    private final JTable table = Ui.table(model, false, COST, PRICE, QTY, VALUE);
    private final JLabel totalLabel = Ui.note(" ");

    PerpetualInventoryPanel(){
        add(buildHeader(), BorderLayout.NORTH);
        add(Ui.scroll(table, "Products"), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        refresh();
    }

    private JPanel buildHeader(){
        JPanel p = new JPanel(new BorderLayout());
        p.add(Ui.note("Perpetual inventory: every receipt and sale posts to the general journal automatically."), BorderLayout.NORTH);
        totalLabel.setFont(totalLabel.getFont().deriveFont(java.awt.Font.BOLD));
        p.add(totalLabel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildButtons(){
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addProduct = new JButton("Add Product");
        JButton receive = new JButton("Receive Stock");
        JButton sell = new JButton("Sell");
        addProduct.addActionListener(e -> onAddProduct());
        receive.addActionListener(e -> onReceive());
        sell.addActionListener(e -> onSell());
        p.add(addProduct);
        p.add(receive);
        p.add(sell);
        return p;
    }

    // ---------- Add Product ----------

    private void onAddProduct(){
        JTextField id = new JTextField(12);
        JTextField name = new JTextField(18);
        JTextField cost = new JTextField(10);
        JTextField price = new JTextField(10);
        JPanel form = form(
                "Product ID", id,
                "Name", name,
                "Cost price", cost,
                "Selling price", price);

        int ok = JOptionPane.showConfirmDialog(this, form, "Add Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(ok != JOptionPane.OK_OPTION) return;

        BigDecimal c = parseMoney(cost.getText());
        BigDecimal s = parseMoney(price.getText());
        if(c == null || s == null){
            error("Enter cost and selling price as numbers.");
            return;
        }
        String err = PerpetualInventory.get().addProduct(id.getText(), name.getText(), c, s);
        if(err != null){ error(err); return; }
        refresh();
    }

    // ---------- Receive Stock ----------

    private void onReceive(){
        Product p = selectedProduct();
        if(p == null){ error("Select a product first."); return; }

        JTextField qty = new JTextField("1", 8);
        JTextField unitCost = new JTextField(p.costPrice().toPlainString(), 10);
        JTextField date = new JTextField(LocalDate.now().toString(), 10);
        JComboBoxString payment = new JComboBoxString(new String[]{"Cash", "On account"});

        JPanel form = form(
                "Product", new JLabel(p.id() + " - " + p.name()),
                "Quantity", qty,
                "Unit cost", unitCost,
                "Date (yyyy-MM-dd)", date,
                "Payment", payment);

        int ok = JOptionPane.showConfirmDialog(this, form, "Receive Stock",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(ok != JOptionPane.OK_OPTION) return;

        Integer q = parseInt(qty.getText());
        BigDecimal uc = parseMoney(unitCost.getText());
        LocalDate d = parseDate(date.getText());
        if(q == null || q <= 0){ error("Quantity must be a positive whole number."); return; }
        if(uc == null){ error("Enter a valid unit cost."); return; }
        if(d == null){ error("Enter the date as yyyy-MM-dd."); return; }

        boolean onAccount = payment.getSelectedIndex() == 1;
        String err = PerpetualInventory.get().receiveStock(p.id(), q, uc, d, onAccount);
        if(err != null){ error(err); return; }
        refresh();
    }

    // ---------- Sell ----------

    private void onSell(){
        Product p = selectedProduct();
        if(p == null){ error("Select a product first."); return; }

        JTextField qty = new JTextField("1", 8);
        JTextField unitPrice = new JTextField(p.sellingPrice().toPlainString(), 10);
        JTextField date = new JTextField(LocalDate.now().toString(), 10);
        JComboBoxString payment = new JComboBoxString(new String[]{"Cash", "On account"});

        JPanel form = form(
                "Product", new JLabel(p.id() + " - " + p.name()),
                "On hand", new JLabel(String.valueOf(p.quantityOnHand())),
                "Quantity", qty,
                "Unit price", unitPrice,
                "Date (yyyy-MM-dd)", date,
                "Payment", payment);

        int ok = JOptionPane.showConfirmDialog(this, form, "Sell",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(ok != JOptionPane.OK_OPTION) return;

        Integer q = parseInt(qty.getText());
        BigDecimal up = parseMoney(unitPrice.getText());
        LocalDate d = parseDate(date.getText());
        if(q == null || q <= 0){ error("Quantity must be a positive whole number."); return; }
        if(up == null){ error("Enter a valid unit price."); return; }
        if(d == null){ error("Enter the date as yyyy-MM-dd."); return; }

        boolean onAccount = payment.getSelectedIndex() == 1;
        String err = PerpetualInventory.get().sell(p.id(), q, up, d, onAccount);
        if(err != null){ error(err); return; }
        refresh();
    }

    private Product selectedProduct(){
        int row = table.getSelectedRow();
        if(row < 0) return null;
        String id = model.getValueAt(row, ID).toString();
        return PerpetualInventory.get().inventory().findProduct(id);
    }

    private static JPanel form(Object... labelFieldPairs){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 4);
        c.anchor = GridBagConstraints.WEST;
        for(int i = 0; i < labelFieldPairs.length; i += 2){
            c.gridx = 0; c.gridy = i / 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
            p.add(new JLabel(labelFieldPairs[i].toString()), c);
            c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
            p.add((java.awt.Component) labelFieldPairs[i + 1], c);
        }
        return p;
    }

    private static BigDecimal parseMoney(String text){
        try{
            return new BigDecimal(text.replace(",", "").trim()).setScale(2, RoundingMode.HALF_UP);
        }catch(NumberFormatException ex){
            return null;
        }
    }

    private static Integer parseInt(String text){
        try{
            return Integer.parseInt(text.trim());
        }catch(NumberFormatException ex){
            return null;
        }
    }

    private static LocalDate parseDate(String text){
        try{
            return LocalDate.parse(text.trim());
        }catch(DateTimeParseException ex){
            return null;
        }
    }

    private void error(String message){
        JOptionPane.showMessageDialog(this, message, "Perpetual Inventory", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    void refresh(){
        model.setRowCount(0);
        for(Product p : PerpetualInventory.get().inventory().getAllProducts()){
            BigDecimal value = p.costPrice().multiply(BigDecimal.valueOf(p.quantityOnHand()));
            model.addRow(new Object[]{
                    p.id(),
                    p.name(),
                    Ui.money(p.costPrice()),
                    Ui.money(p.sellingPrice()),
                    p.quantityOnHand(),
                    Ui.money(value)
            });
        }
        totalLabel.setText("Total inventory value: " + Ui.money(PerpetualInventory.get().totalValue()));
    }

    private static final class JComboBoxString extends javax.swing.JComboBox<String> {
        JComboBoxString(String[] items){ super(items); }
    }
}
