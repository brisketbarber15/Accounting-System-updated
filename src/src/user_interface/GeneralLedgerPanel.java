package user_interface;

import functions.Books;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import system.Account;
import system.TAccount;
import statics.GeneralLedger;

class GeneralLedgerPanel extends Page {
    private static final String ALL = "All accounts with activity";
    private final JComboBox<String> filter = new JComboBox<>();
    private final JPanel content = new JPanel();

    GeneralLedgerPanel(){
        filter.addItem(ALL);
        for(Account a : Books.get().getAccounts()){
            filter.addItem(a.code() + " " + a.name());
        }
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Show:"));
        top.add(filter);
        add(top, BorderLayout.NORTH);

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(content);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        filter.addActionListener(e -> refresh());
        refresh();
    }

    @Override
    void refresh(){
        content.removeAll();
        GeneralLedger ledger = Books.get().ledger();
        String selected = (String) filter.getSelectedItem();
        if(selected == null || ALL.equals(selected)){
            for(TAccount taccount : ledger.getActive()){
                content.add(new TAccountView(taccount));
            }
        }else{
            content.add(new TAccountView(ledger.get(Integer.parseInt(selected.split(" ")[0]))));
        }
        content.revalidate();
        content.repaint();
    }
}
