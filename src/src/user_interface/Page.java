package user_interface;

/** A screen shown in the main display area of MainFrame. */
abstract class Page extends javax.swing.JPanel {

    Page(){
        super(new java.awt.BorderLayout(8, 8));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    /** Reloads what is shown from the books; called every time the screen is opened. */
    abstract void refresh();
}
