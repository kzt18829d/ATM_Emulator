package org.kzt18829d.core.type;

import java.util.Objects;

public class TransactionID {
    private final String transactionID;

    public TransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionID that = (TransactionID) o;
        return Objects.equals(transactionID, that.transactionID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionID) * 27;
    }
}
