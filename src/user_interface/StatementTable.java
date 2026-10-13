package user_interface;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import statics.FinancialStatements.Line;
import statics.FinancialStatements.Style;

/** Shows a financial statement: headings, indented items, and ruled subtotals and totals. */
final class StatementTable extends JScrollPane {
    private final DefaultTableModel model = Ui.readOnlyModel("Account", "Amount");
    private final JTable table = new JTable(model);
    private List<Line> lines = List.of();

    StatementTable(){
        table.setTableHeader(null);
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setRowSelectionAllowed(false);
        table.setFocusable(false);
        table.getColumnModel().getColumn(1).setMaxWidth(190);
        table.getColumnModel().getColumn(1).setPreferredWidth(190);
        TableCellRenderer renderer = this::render;
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);
        setViewportView(table);
    }

    private Component render(JTable t, Object value, boolean selected, boolean focus, int row, int column){
        Style style = lines.get(row).style();
        JLabel label = new JLabel(value == null ? "" : value.toString());
        label.setOpaque(false);
        boolean bold = style != Style.ITEM;
        label.setFont(t.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN));
        label.setHorizontalAlignment(column == 1 ? SwingConstants.RIGHT : SwingConstants.LEFT);
        int left = column == 0 ? (style == Style.ITEM ? 28 : 6) : 0;
        if(style == Style.SUBTOTAL){
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, t.getForeground()),
                    BorderFactory.createEmptyBorder(0, left, 0, 8)));
        }else if(style == Style.TOTAL){
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 3, 0, t.getForeground()),
                    BorderFactory.createEmptyBorder(0, left, 0, 8)));
        }else{
            label.setBorder(BorderFactory.createEmptyBorder(0, left, 0, 8));
        }
        return label;
    }

    void setLines(List<Line> newLines){
        lines = newLines;
        model.setRowCount(0);
        for(Line line : newLines){
            model.addRow(new Object[]{line.label(), line.amount() == null ? "" : Ui.money(line.amount())});
        }
    }
}
