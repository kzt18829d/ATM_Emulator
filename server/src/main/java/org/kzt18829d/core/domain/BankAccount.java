package org.kzt18829d.core.domain;

import org.kzt18829d.core.type.BankAccountID;
import org.kzt18829d.core.type.BankAccountName;
import org.kzt18829d.core.type.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class BankAccount {
    private BankAccountName bankAccountName;
    private final BankAccountID accountID;
    private final CurrencyType currencyType;
    private final LocalDate registryDate;
    private BigDecimal currencyValue;
    private final UUID ownerID;

    public BankAccount(UUID ownerID, BankAccountID accountID, CurrencyType currencyType) {
        this.ownerID = ownerID;
        this.accountID = accountID;
        this.currencyType = currencyType;
        this.currencyValue = BigDecimal.ZERO;
        this.registryDate = LocalDate.now();
    }

    public BankAccount(UUID ownerID, BankAccountID accountID, CurrencyType currencyType, BigDecimal currencyValue, LocalDate registryDate) {
        this.ownerID = ownerID;
        this.accountID = accountID;
        this.currencyType = currencyType;
        this.currencyValue = currencyValue;
        this.registryDate = registryDate;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public BankAccountID getAccountID() {
        return accountID;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public BigDecimal getCurrencyValue() {
        return currencyValue;
    }

    public BankAccountName getBankAccountName() {
        return bankAccountName;
    }

    public void replenishAccount(BigDecimal value) {
        this.currencyValue = currencyValue.add(value);
    }

    public LocalDate getRegistryDate() {
        return registryDate;
    }

    public void withdrawAccount(BigDecimal value) {
        if (currencyValue.compareTo(value) < 0) throw new IllegalArgumentException("Value the biggest of bankAccount");
        this.currencyValue = currencyValue.subtract(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountID, that.accountID) && currencyType == that.currencyType && Objects.equals(registryDate, that.registryDate) && Objects.equals(currencyValue, that.currencyValue);
    }

    @Override
    public int hashCode() {
        return 27 * accountID.hashCode() + currencyType.hashCode() + registryDate.hashCode();
    }
}
