package org.kzt18829d.core.type;

import java.util.Objects;

public class BankAccountName {
    private String accountName;

    public BankAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountName that = (BankAccountName) o;
        return Objects.equals(accountName, that.accountName);
    }

    @Override
    public int hashCode() {
        return 31 * accountName.hashCode();
    }
}
