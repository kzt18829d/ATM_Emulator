package org.kzt18829d.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        this.bankAccountName = new BankAccountName("Default bank account");
        this.registryDate = LocalDate.now();
    }

    @JsonCreator
    public BankAccount(
            @JsonProperty("ownerUUID") UUID ownerID,
            @JsonProperty("bankAccountID") BankAccountID accountID,
            @JsonProperty("currencyType") CurrencyType currencyType,
            @JsonProperty("amount") BigDecimal currencyValue,
            @JsonProperty("bankAccountName") BankAccountName bankAccountName,
            @JsonProperty("registryDate") LocalDate registryDate) {
        this.ownerID = ownerID;
        this.accountID = accountID;
        this.currencyType = currencyType;
        this.currencyValue = currencyValue;
        this.bankAccountName = bankAccountName;
        this.registryDate = registryDate;
    }

    @JsonGetter("ownerUUID")
    public UUID getOwnerID() {
        return ownerID;
    }

    @JsonGetter("bankAccountID")
    public BankAccountID getAccountID() {
        return accountID;
    }

    @JsonGetter("currencyType")
    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    @JsonGetter("amount")
    public BigDecimal getCurrencyValue() {
        return currencyValue;
    }

    @JsonGetter("bankAccountName")
    public BankAccountName getBankAccountName() {
        return bankAccountName;
    }

    @JsonGetter("registryDate")
    public LocalDate getRegistryDate() {
        return registryDate;
    }

    public void setBankAccountName(BankAccountName bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public void replenishAccount(BigDecimal value) {
        this.currencyValue = currencyValue.add(value);
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
