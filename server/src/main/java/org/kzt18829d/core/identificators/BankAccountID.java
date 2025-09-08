package org.kzt18829d.core.identificators;

import org.kzt18829d.exception.IdentificatorException;

import java.util.Objects;

public class BankAccountID {
    private final String accountID;

    public BankAccountID(String accountID) {
        if (accountID == null || accountID.isBlank())
            throw new IdentificatorException("Bank account id cannot be null or empty");
        this.accountID = accountID.trim();
    }

    public String getAccountID() {
        return accountID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountID that = (BankAccountID) o;
        return Objects.equals(accountID, that.accountID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountID) * 23;
    }
}
