package user_interface;

import functions.Books;
import javax.swing.table.DefaultTableModel;
import system.Account;

class ChartOfAccountsPanel extends Page {
    private final DefaultTableModel model = Ui.readOnlyModel("Code", "Account Title", "Type", "Normal Balance");

    ChartOfAccountsPanel(){
        javax.swing.JTable table = Ui.table(model, false);
        Ui.width(table, 0, 70);
        Ui.width(table, 2, 160);
        Ui.width(table, 3, 130);
        add(Ui.scroll(table, null), java.awt.BorderLayout.CENTER);
        add(Ui.note("Contra accounts carry the opposite normal balance of their parent account type."), java.awt.BorderLayout.SOUTH);
        refresh();
    }

    @Override
    void refresh(){
        model.setRowCount(0);
        for(Account a : Books.get().getAccounts()){
            String type = a.type().name().replace("OWNERSEQUITY", "OWNER'S EQUITY");
            type = type.charAt(0) + type.substring(1).toLowerCase() + (a.isContra() ? " (contra)" : "");
            String normal = a.getNormalBalance().name();
            model.addRow(new Object[]{a.code(), a.name(), type, normal.charAt(0) + normal.substring(1).toLowerCase()});
        }
    }
}
