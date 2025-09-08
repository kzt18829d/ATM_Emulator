package org.kzt18829d.core.identificators;

import org.kzt18829d.exception.IdentificatorException;

public class TransactionID {
    private final String transactionID;

    public TransactionID(String transactionID) {
        if (transactionID == null || transactionID.isBlank())
            throw new IdentificatorException("Transaction id cannot be null or empty");
        this.transactionID = transactionID.trim();
    }

    public String getTransactionID() {
        return transactionID;
    }
}
