/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system;

/**
 *
 * @author Ryan
 */
public enum AccountType {
    ASSET(NormalBalance.DEBIT),
    LIABILITY(NormalBalance.CREDIT),
    OWNERSEQUITY(NormalBalance.CREDIT), 
    REVENUE(NormalBalance.CREDIT),
    COST(NormalBalance.DEBIT),
    EXPENSE(NormalBalance.DEBIT);

    private final NormalBalance normalBalance;

    AccountType(NormalBalance normalBalance){
        this.normalBalance = normalBalance;
    }

    public NormalBalance getNormalBalance(boolean isContra) {
        if (!isContra) {
            return normalBalance;
        }
        return normalBalance == NormalBalance.DEBIT ? NormalBalance.CREDIT : NormalBalance.DEBIT;
    }
}
