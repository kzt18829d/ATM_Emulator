package org.kzt18829d.core.type;

import java.util.Objects;

public class BankAccountID {
    private final String id;

    public BankAccountID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountID that = (BankAccountID) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 29 * id.hashCode();
    }
}
