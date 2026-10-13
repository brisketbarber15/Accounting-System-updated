package user_interface;

import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Small helpers shared by the screens. */
final class Ui {

    private Ui(){
    }

    /** 1,234.50 and (1,234.50) for negatives; empty for null. */
    static String money(BigDecimal value){
        if(value == null){
            return "";
        }
        if(value.signum() < 0){
            return "(" + String.format("%,.2f", value.negate()) + ")";
        }
        return String.format("%,.2f", value);
    }

    static DefaultTableModel readOnlyModel(String... columns){
        return new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
    }

    /** Draws numeric columns right-aligned and, optionally, the last row (a total) in bold. */
    private static final class CellStyle extends DefaultTableCellRenderer {
        private final boolean right;
        private final boolean boldLastRow;

        CellStyle(boolean right, boolean boldLastRow){
            this.right = right;
            this.boldLastRow = boldLastRow;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column){
            Component c = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setHorizontalAlignment(right ? SwingConstants.RIGHT : SwingConstants.LEFT);
            boolean bold = boldLastRow && row == table.getRowCount() - 1;
            c.setFont(table.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN));
            return c;
        }
    }

    static JTable table(DefaultTableModel model, boolean boldLastRow, int... rightAlignedColumns){
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        for(int i = 0; i < table.getColumnCount(); i++){
            boolean right = false;
            for(int r : rightAlignedColumns){
                if(r == i){
                    right = true;
                }
            }
            table.getColumnModel().getColumn(i).setCellRenderer(new CellStyle(right, boldLastRow));
        }
        return table;
    }

    /** Fixes a column to the given width; the remaining columns share the rest. */
    static void width(JTable table, int column, int width){
        javax.swing.table.TableColumn c = table.getColumnModel().getColumn(column);
        c.setMinWidth(width);
        c.setMaxWidth(width);
        c.setPreferredWidth(width);
    }

    static JScrollPane scroll(JTable table, String title){
        JScrollPane pane = new JScrollPane(table);
        if(title != null){
            pane.setBorder(BorderFactory.createTitledBorder(title));
        }
        return pane;
    }

    static JLabel note(String text){
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        return label;
    }
}
