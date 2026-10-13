package statics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import system.TAccount;

/**
 * Statement of Financial Performance (income statement), Statement of Changes in Equity and
 * Statement of Financial Position (balance sheet), built from the unadjusted general ledger.
 *
 * Inventory is periodic: cost of goods sold = beginning inventory + net purchases - ending inventory.
 * The ending inventory is the ledger balance unless an inventory count is supplied.
 */
public class FinancialStatements {
    public enum Style { HEADING, ITEM, SUBTOTAL, TOTAL }

    public record Line(String label, BigDecimal amount, Style style) {}

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final GeneralLedger ledger;
    private final BigDecimal openingInventory;
    private final BigDecimal openingCapital;
    private final BigDecimal endingInventory;
    private final BigDecimal netSales;
    private final BigDecimal netPurchases;
    private final BigDecimal costOfGoodsSold;
    private final BigDecimal grossProfit;
    private final BigDecimal operatingExpenses;
    private final BigDecimal netIncome;
    private final BigDecimal additionalInvestments;
    private final BigDecimal withdrawals;
    private final BigDecimal endingCapital;
    private final BigDecimal totalAssets;
    private final BigDecimal totalLiabilities;

    /** @param endingInventoryCount the counted ending inventory, or null to use the ledger balance */
    public FinancialStatements(GeneralLedger ledger, BigDecimal openingInventory, BigDecimal openingCapital, BigDecimal endingInventoryCount){
        this.ledger = ledger;
        this.openingInventory = openingInventory;
        this.openingCapital = openingCapital;
        this.endingInventory = endingInventoryCount != null ? endingInventoryCount : bal(120);

        netSales = bal(400).subtract(bal(401)).subtract(bal(402));
        netPurchases = bal(500).add(bal(501)).subtract(bal(502)).subtract(bal(503));
        costOfGoodsSold = openingInventory.add(netPurchases).subtract(endingInventory);
        grossProfit = netSales.subtract(costOfGoodsSold);
        operatingExpenses = sum(expenseLines());
        netIncome = grossProfit.subtract(operatingExpenses).add(bal(410)).subtract(bal(650));

        additionalInvestments = bal(300).subtract(openingCapital);
        withdrawals = bal(301);
        endingCapital = bal(300).add(netIncome).subtract(withdrawals);

        totalAssets = sum(currentAssets()).add(sum(propertyAndEquipment())).add(sum(otherAssets()));
        totalLiabilities = sum(liabilityLines());
    }

    private BigDecimal bal(int code){
        return ledger.balance(code);
    }

    private BigDecimal net(int code, int contraCode){
        return bal(code).subtract(bal(contraCode));
    }

    private static BigDecimal sum(List<Line> lines){
        BigDecimal total = ZERO;
        for(Line line : lines){
            total = total.add(line.amount());
        }
        return total;
    }

    private static void add(List<Line> out, String label, BigDecimal amount){
        if(amount.signum() != 0){
            out.add(new Line(label, amount, Style.ITEM));
        }
    }

    private List<Line> expenseLines(){
        List<Line> out = new ArrayList<>();
        for(TAccount taccount : ledger.getAll()){
            int code = taccount.getAccount().code();
            if(code >= 600 && code <= 640){
                add(out, taccount.getAccount().name(), taccount.getNetBalance());
            }
        }
        return out;
    }

    private List<Line> liabilityLines(){
        List<Line> out = new ArrayList<>();
        for(TAccount taccount : ledger.getAll()){
            int code = taccount.getAccount().code();
            if(code >= 200 && code < 300){
                add(out, taccount.getAccount().name(), taccount.getNetBalance());
            }
        }
        return out;
    }

    private List<Line> currentAssets(){
        List<Line> out = new ArrayList<>();
        add(out, "Cash", bal(100));
        add(out, "Petty cash", bal(101));
        add(out, "Notes receivable", bal(110));
        add(out, "Interest receivable", bal(111));
        add(out, "Accounts receivable, net of allowance", net(112, 113));
        add(out, "Merchandise inventory", endingInventory);
        add(out, "Supplies", bal(130));
        add(out, "Prepaid insurance", bal(140));
        add(out, "Prepaid rent", bal(141));
        return out;
    }

    private List<Line> propertyAndEquipment(){
        List<Line> out = new ArrayList<>();
        add(out, "Furniture and fixtures, net", net(150, 151));
        add(out, "Equipment, net", net(152, 153));
        add(out, "Service vehicle, net", net(154, 155));
        return out;
    }

    private List<Line> otherAssets(){
        List<Line> out = new ArrayList<>();
        add(out, "Rent deposit", bal(160));
        add(out, "Utilities deposit", bal(161));
        return out;
    }

    /** Cost of goods sold schedule (also used by the Inventory page). */
    public List<Line> costOfGoodsSoldSchedule(){
        List<Line> out = new ArrayList<>();
        out.add(new Line("Merchandise inventory, beginning", openingInventory, Style.ITEM));
        add(out, "Purchases", bal(500));
        add(out, "Transportation-in", bal(501));
        add(out, "Less: purchases discounts", bal(502).negate());
        add(out, "Less: purchase returns and allowances", bal(503).negate());
        out.add(new Line("Net cost of purchases", netPurchases, Style.SUBTOTAL));
        out.add(new Line("Goods available for sale", openingInventory.add(netPurchases), Style.SUBTOTAL));
        out.add(new Line("Less: merchandise inventory, ending", endingInventory.negate(), Style.ITEM));
        out.add(new Line("Cost of goods sold", costOfGoodsSold, Style.TOTAL));
        return out;
    }

    public List<Line> incomeStatement(){
        List<Line> out = new ArrayList<>();
        out.add(new Line("Revenue", null, Style.HEADING));
        add(out, "Sales", bal(400));
        add(out, "Less: sales discounts", bal(401).negate());
        add(out, "Less: sales returns and allowances", bal(402).negate());
        out.add(new Line("Net sales", netSales, Style.SUBTOTAL));
        out.add(new Line("Cost of goods sold", null, Style.HEADING));
        out.addAll(costOfGoodsSoldSchedule());
        out.add(new Line("Gross profit", grossProfit, Style.SUBTOTAL));
        out.add(new Line("Operating expenses", null, Style.HEADING));
        out.addAll(expenseLines());
        out.add(new Line("Total operating expenses", operatingExpenses, Style.SUBTOTAL));
        out.add(new Line("Income from operations", grossProfit.subtract(operatingExpenses), Style.SUBTOTAL));
        add(out, "Interest income", bal(410));
        add(out, "Less: interest expense", bal(650).negate());
        out.add(new Line("Net income (loss)", netIncome, Style.TOTAL));
        return out;
    }

    public List<Line> changesInEquity(){
        List<Line> out = new ArrayList<>();
        out.add(new Line("Capital, beginning", openingCapital, Style.ITEM));
        add(out, "Additional investments (net)", additionalInvestments);
        out.add(new Line("Net income (loss)", netIncome, Style.ITEM));
        add(out, "Less: withdrawals", withdrawals.negate());
        out.add(new Line("Capital, ending", endingCapital, Style.TOTAL));
        return out;
    }

    public List<Line> balanceSheet(){
        List<Line> out = new ArrayList<>();
        out.add(new Line("Assets", null, Style.HEADING));
        out.add(new Line("Current assets", null, Style.HEADING));
        out.addAll(currentAssets());
        out.add(new Line("Total current assets", sum(currentAssets()), Style.SUBTOTAL));
        out.add(new Line("Property and equipment", null, Style.HEADING));
        out.addAll(propertyAndEquipment());
        out.add(new Line("Total property and equipment", sum(propertyAndEquipment()), Style.SUBTOTAL));
        out.add(new Line("Other assets", null, Style.HEADING));
        out.addAll(otherAssets());
        out.add(new Line("Total assets", totalAssets, Style.TOTAL));
        out.add(new Line("Liabilities", null, Style.HEADING));
        out.addAll(liabilityLines());
        out.add(new Line("Total liabilities", totalLiabilities, Style.SUBTOTAL));
        out.add(new Line("Owner\u2019s equity", null, Style.HEADING));
        out.add(new Line(ledger.get(300).getAccount().name() + " (ending)", endingCapital, Style.ITEM));
        out.add(new Line("Total liabilities and owner\u2019s equity", totalLiabilities.add(endingCapital), Style.TOTAL));
        return out;
    }

    public boolean isBalanced(){
        return totalAssets.compareTo(totalLiabilities.add(endingCapital)) == 0;
    }

    public BigDecimal getNetIncome(){ return netIncome; }
    public BigDecimal getEndingCapital(){ return endingCapital; }
    public BigDecimal getTotalAssets(){ return totalAssets; }
    public BigDecimal getTotalLiabilities(){ return totalLiabilities; }
    public BigDecimal getEndingInventory(){ return endingInventory; }
}
