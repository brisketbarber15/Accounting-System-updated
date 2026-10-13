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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<String, Product> products = new HashMap<>();

    public void addProduct(Product p) {
        products.put(p.id(), p);
    }

    public Product findProduct(String id) {
        return products.get(id);
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public BigDecimal getTotalValue() {
        return products.values().stream()
                .map(p -> p.costPrice().multiply(BigDecimal.valueOf(p.quantityOnHand())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void display() {
        System.out.println("\nInventory");
        System.out.println("-----------------------");
        if (products.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        for (Product p : products.values()) {
            System.out.println(p);
        }
        System.out.println("Total value: " + getTotalValue());
    }
}