/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system;

/**
 *
 * @author Brent
 */
import java.math.BigDecimal;

public class Product {
    private final String id;
    private final String name;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int quantityOnHand;

    public Product(String id, String name, BigDecimal costPrice, BigDecimal sellingPrice, int quantityOnHand) {
        this.id = id;
        this.name = name;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.quantityOnHand = quantityOnHand;
    }

    public String id() { return id; }
    public String name() { return name; }
    public BigDecimal costPrice() { return costPrice; }
    public BigDecimal sellingPrice() { return sellingPrice; }
    public int quantityOnHand() { return quantityOnHand; }

    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public void addStock(int qty) { this.quantityOnHand += qty; }
    public void removeStock(int qty) { this.quantityOnHand -= qty; }

    @Override
    public String toString() {
        return id + " - " + name + " (qty: " + quantityOnHand
                + ", cost: " + costPrice + ", price: " + sellingPrice + ")";
    }
}