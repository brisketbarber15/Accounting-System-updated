package statics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Ryan
 */
import java.util.List;
import java.util.ArrayList;
import system.*;
public class ChartofAccounts {
    private List<Account> accounts;
    
    int[] assetCodes = {100, 101, 110, 111, 112, 113, 120, 130, 140, 141, 150, 151, 152, 153, 154, 155, 160, 161};
    String[] assetNames = {"Cash", "Petty Cash", "Notes Receivable", "Interest Receivable", "Accounts Receivable", "Allowance for Uncollectible Accounts", "Merchandise Inventory", "Supplies", "Prepaid Insurance", "Prepaid Rent", "Furniture and Fixtures", "Accumulated Depreciated - Furniture and Fixtures", "Equipment", "Accumulated Depreciated - Equipment", "Service Vehicle", "Accumulated Depreciated - Service Vehicle", "Rent Deposit", "Utilities Deposit"};

    int[] liabilityCodes = {200, 201, 202, 210, 211, 212, 213, 214, 220, 221, 230};
    String[] liabilityNames = {"Notes Payable", "Interest Payable", "Accounts Payable", "Salaries Payable", "SSS and EC Contributions Payable", "Philhealth Contributions Payable", "Pag-IBIG Contributions Payable", "Withholding Taxes Payable", "Rent Payable", "Utilities Payable", "VAT Payable"};

    int[] ownersEquityCodes = {300, 301, 302};
    String[] ownersEquityNames = {"Therese Ballada, Capital", "Therese Ballada, Withdrawals", "Income Summary"};

    int[] revenueCodes = {400, 401, 402, 410};
    String[] revenueNames = {"Sales", "Sales Discounts", "Sales Returns and Allowances", "Interest Income"};

    int[] costCodes = {500, 501, 502, 503};
    String[] costNames = {"Purchases", "Transportation-in", "Purchases Discounts", "Purchase Return and Allowances"};

    int[] expenseCodes = {600, 601, 602, 603, 604, 605, 610, 611, 612, 613, 620, 630, 631, 632, 640, 650};
    String[] expenseNames = {"Advertising Expense", "Supplies Expense", "Insurance Expense", "Rent Expense", "Utilities Expense", "Repairs and Maintenance Expense", "Salaries Expense", "SSS and EC Contributions Expense", "Philhealth Contributions Expense", "Pag-IBIG Contributions Expense", "Uncollectible Accounts Expense", "Depreciation Expense - Furniture and Fixtures", "Depreciation Expense - Equipment", "Depreciation Expense - Service Vehicle", "Miscellaneous Expense", "Interest Expense"};

    public ChartofAccounts(){
        accounts = new ArrayList<>();
        int i;
        
        for(i=0; i<assetCodes.length; i++){
            if(assetCodes[i] == 113 || assetCodes[i] == 151 || assetCodes[i] == 153 || assetCodes[i] == 155){
                accounts.add(new Account(assetCodes[i], assetNames[i], AccountType.ASSET, true));
                continue;
            }
            accounts.add(new Account(assetCodes[i], assetNames[i], AccountType.ASSET, false));
        }
        
        for(i=0; i<liabilityCodes.length; i++){
            accounts.add(new Account(liabilityCodes[i], liabilityNames[i], AccountType.LIABILITY, false));
        }
        
        for(i=0; i<ownersEquityCodes.length; i++){
            if(ownersEquityCodes[i] == 301){
                accounts.add(new Account(ownersEquityCodes[i], ownersEquityNames[i], AccountType.OWNERSEQUITY, true));
                continue;
            }
            accounts.add(new Account(ownersEquityCodes[i], ownersEquityNames[i], AccountType.OWNERSEQUITY, false));
        }
        
        for(i=0; i<revenueCodes.length; i++){
            if(revenueCodes[i] == 401 || revenueCodes[i] == 402){
                accounts.add(new Account(revenueCodes[i], revenueNames[i], AccountType.REVENUE, true));
                continue;
            }
            accounts.add(new Account(revenueCodes[i], revenueNames[i], AccountType.REVENUE, false));
        }
        
        for(i=0; i<costCodes.length; i++){
            if(costCodes[i] == 502 || costCodes[i] == 503){
                accounts.add(new Account(costCodes[i], costNames[i], AccountType.COST, true));
                continue;
            }
            accounts.add(new Account(costCodes[i], costNames[i], AccountType.COST, false));
        }
        
        for(i=0; i<expenseCodes.length; i++){
            accounts.add(new Account(expenseCodes[i], expenseNames[i], AccountType.EXPENSE, false));
        }
    }
    
    public void display(){
        System.out.println("Chart of Accounts\n");
        System.out.println(accounts.get(0).type().toString());
        for(int i=0; i<accounts.size(); i++){
            System.out.println(accounts.get(i).code() + " " + accounts.get(i).name());
            if(i != accounts.size()-1 && accounts.get(i).code() / 100 != accounts.get(i+1).code() / 100){
                System.out.println("\n" + accounts.get(i+1).type() + "\n");
            }
        }
    }
    
    public List<Account> getAccounts(){
        return accounts;
    }
}
