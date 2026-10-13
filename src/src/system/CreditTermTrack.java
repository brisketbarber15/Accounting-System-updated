package system;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tracks open credit invoices (accounts receivable / payable) under 2/10, n/30 terms:
 * a 2% discount if settled within 10 days of the invoice date, full amount due within 30 days.
 */
public class CreditTermTrack {
    public static final int DISCOUNT_DAYS = 10;
    public static final int CREDIT_DAYS = 30;
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.02");

    // keyed by invoice date; TreeMap keeps them in date order
    private final Map<LocalDate, BigDecimal> accountReceivables = new TreeMap<>();
    private final Map<LocalDate, BigDecimal> accountPayables = new TreeMap<>();

    // amounts on the same date are added together instead of overwriting each other
    public void addAccountReceivable(LocalDate date, BigDecimal amount){
        accountReceivables.merge(date, amount, BigDecimal::add);
    }

    public void addAccountPayable(LocalDate date, BigDecimal amount){
        accountPayables.merge(date, amount, BigDecimal::add);
    }

    // same behaviour as before: null if the invoice date is not found or the amount is greater than the open balance
    public BigDecimal getAccountReceivableAmount(LocalDate dateToFind, LocalDate currentDate, BigDecimal paidAmount){
        return discountedAmount(accountReceivables, dateToFind, currentDate, paidAmount);
    }

    public BigDecimal getAccountPayableAmount(LocalDate dateToFind, LocalDate currentDate, BigDecimal paidAmount){
        return discountedAmount(accountPayables, dateToFind, currentDate, paidAmount);
    }

    private static BigDecimal discountedAmount(Map<LocalDate, BigDecimal> book, LocalDate dateToFind, LocalDate currentDate, BigDecimal paidAmount){
        BigDecimal open = book.get(dateToFind);
        if(open == null || open.compareTo(paidAmount) < 0){
            return null;
        }
        if(qualifiesForDiscount(dateToFind, currentDate)){
            return paidAmount.multiply(BigDecimal.ONE.subtract(DISCOUNT_RATE));
        }
        return paidAmount;
    }

    public static boolean qualifiesForDiscount(LocalDate invoiceDate, LocalDate paymentDate){
        long daysAgo = ChronoUnit.DAYS.between(invoiceDate, paymentDate);
        return daysAgo >= 0 && daysAgo <= DISCOUNT_DAYS;
    }

    public static BigDecimal discountOn(BigDecimal amount){
        return amount.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal receivableBalance(LocalDate invoiceDate){
        return accountReceivables.getOrDefault(invoiceDate, BigDecimal.ZERO);
    }

    public BigDecimal payableBalance(LocalDate invoiceDate){
        return accountPayables.getOrDefault(invoiceDate, BigDecimal.ZERO);
    }

    // reduce an open invoice by the amount settled; a fully settled invoice is removed
    public void settleReceivable(LocalDate invoiceDate, BigDecimal amount){
        settle(accountReceivables, invoiceDate, amount);
    }

    public void settlePayable(LocalDate invoiceDate, BigDecimal amount){
        settle(accountPayables, invoiceDate, amount);
    }

    private static void settle(Map<LocalDate, BigDecimal> book, LocalDate invoiceDate, BigDecimal amount){
        book.computeIfPresent(invoiceDate, (date, open) -> {
            BigDecimal remaining = open.subtract(amount);
            return remaining.signum() <= 0 ? null : remaining;
        });
    }

    public Map<LocalDate, BigDecimal> getAccountReceivables(){
        return Collections.unmodifiableMap(accountReceivables);
    }

    public Map<LocalDate, BigDecimal> getAccountPayables(){
        return Collections.unmodifiableMap(accountPayables);
    }

    public CreditTermTrack copy(){
        CreditTermTrack copy = new CreditTermTrack();
        copy.accountReceivables.putAll(accountReceivables);
        copy.accountPayables.putAll(accountPayables);
        return copy;
    }
}
