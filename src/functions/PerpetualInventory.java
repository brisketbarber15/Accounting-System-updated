/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functions;

/**
 *
 * @author Brent
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import system.EntryType;
import system.Inventory;
import system.Product;

public final class PerpetualInventory {

    private static final int CASH = 100;
    private static final int AR = 112;
    private static final int INVENTORY = 120;
    private static final int AP = 202;
    private static final int SALES = 400;
    private static final int COGS = 606;

    private static final PerpetualInventory INSTANCE = new PerpetualInventory();
    public static PerpetualInventory get(){ return INSTANCE; }

    private final Inventory inventory = new Inventory();
    private PerpetualInventory(){}

    public Inventory inventory(){ return inventory; }

    public String addProduct(String id, String name, BigDecimal cost, BigDecimal price){
        if(id == null || id.isBlank()) return "Enter a product ID.";
        if(name == null || name.isBlank()) return "Enter a product name.";
        if(cost == null || cost.signum() < 0) return "Enter a valid cost price (0 or more).";
        if(price == null || price.signum() < 0) return "Enter a valid selling price (0 or more).";
        if(inventory.findProduct(id.trim()) != null) return "A product with that ID already exists.";
        inventory.addProduct(new Product(id.trim(), name.trim(), cost, price, 0));
        return null;
    }

    public String receiveStock(String id, int qty, BigDecimal unitCost, LocalDate date, boolean onAccount){
        Product p = inventory.findProduct(id);
        if(p == null) return "Product not found.";
        if(qty <= 0) return "Quantity must be greater than zero.";
        if(unitCost == null || unitCost.signum() < 0) return "Enter a valid unit cost.";
        if(date == null) return "Enter a valid date.";

        BigDecimal total = unitCost.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);

        List<Books.Line> lines = new ArrayList<>();
        lines.add(new Books.Line(Books.get().findByCode(INVENTORY), total, EntryType.DEBIT, null));
        lines.add(new Books.Line(Books.get().findByCode(onAccount ? AP : CASH), total, EntryType.CREDIT, null));
        String desc = "Received " + qty + " x " + p.name() + (onAccount ? " on account" : " for cash");
        String err = Books.get().post(date, desc, lines);
        if(err != null) return err;

        int oldQty = p.quantityOnHand();
        BigDecimal oldCost = p.costPrice();
        int newQty = oldQty + qty;
        BigDecimal newAvg = oldQty == 0
                ? unitCost.setScale(4, RoundingMode.HALF_UP)
                : oldCost.multiply(BigDecimal.valueOf(oldQty))
                        .add(unitCost.multiply(BigDecimal.valueOf(qty)))
                        .divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP);
        p.setCostPrice(newAvg);
        p.addStock(qty);
        return null;
    }

    /** Sells stock, posts revenue + COGS entries, and reduces quantity. */
    public String sell(String id, int qty, BigDecimal unitPrice, LocalDate date, boolean onAccount){
        Product p = inventory.findProduct(id);
        if(p == null) return "Product not found.";
        if(qty <= 0) return "Quantity must be greater than zero.";
        if(qty > p.quantityOnHand()) return "Not enough stock. On hand: " + p.quantityOnHand();
        if(unitPrice == null || unitPrice.signum() < 0) return "Enter a valid selling price.";
        if(date == null) return "Enter a valid date.";

        BigDecimal revenue = unitPrice.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cogs = p.costPrice().multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);

        List<Books.Line> lines = new ArrayList<>();
        lines.add(new Books.Line(Books.get().findByCode(onAccount ? AR : CASH), revenue, EntryType.DEBIT, null));
        lines.add(new Books.Line(Books.get().findByCode(SALES), revenue, EntryType.CREDIT, null));
        lines.add(new Books.Line(Books.get().findByCode(COGS), cogs, EntryType.DEBIT, null));
        lines.add(new Books.Line(Books.get().findByCode(INVENTORY), cogs, EntryType.CREDIT, null));

        String desc = "Sold " + qty + " x " + p.name() + (onAccount ? " on account" : " for cash");
        String err = Books.get().post(date, desc, lines);
        if(err != null) return err;

        p.removeStock(qty);
        return null;
    }

    public BigDecimal totalValue(){
        return inventory.getTotalValue();
    }
}
