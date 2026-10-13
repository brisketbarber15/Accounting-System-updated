/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system;

/**
 *
 * @author Ryan
 */
public record Account(int code, String name, AccountType type, boolean isContra) { 
    public NormalBalance getNormalBalance() {
        return type.getNormalBalance(isContra);
    }
}
