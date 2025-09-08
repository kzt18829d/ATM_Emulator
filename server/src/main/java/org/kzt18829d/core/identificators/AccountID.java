package org.kzt18829d.core.identificators;

import org.kzt18829d.exception.IdentificatorException;

import java.util.Objects;

public class AccountID {
    private final String accountID;

    public AccountID(String accountID) {
        if (accountID == null || accountID.isBlank())
            throw new IdentificatorException("Account id cannot be null or empty");
        this.accountID = accountID.trim();
    }

    public String getAccountID() {
        return accountID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountID accountID1 = (AccountID) o;
        return Objects.equals(accountID, accountID1.accountID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountID) * 23;
    }
}
