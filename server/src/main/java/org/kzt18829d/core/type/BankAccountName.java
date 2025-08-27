package org.kzt18829d.core.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BankAccountName {
    private String accountName;

    @JsonCreator
    public BankAccountName(@JsonProperty("accountName") String accountName) {
        this.accountName = accountName;
    }

    @JsonGetter("accountName")
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
