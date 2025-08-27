package org.kzt18829d.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.kzt18829d.core.type.BankAccountID;
import org.kzt18829d.core.type.TransactionID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private final TransactionID transactionID;
    private final String sender;
    private final String receiver;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(TransactionID transactionID, BankAccountID sender, BankAccountID receiver, BigDecimal amount) {
        this.transactionID = transactionID;
        this.sender = sender.getID();
        this.receiver = receiver.getID();
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    @JsonCreator
    public Transaction(
            @JsonProperty("transactionID") TransactionID transactionID,
            @JsonProperty("sender") String sender,
            @JsonProperty("receiver") String receiver,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.transactionID = transactionID;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public TransactionID getTransactionID() {
        return transactionID;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionID, that.transactionID) && Objects.equals(sender, that.sender) && Objects.equals(receiver, that.receiver) && Objects.equals(amount, that.amount) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return 31 * transactionID.hashCode() + sender.hashCode() + receiver.hashCode() + amount.hashCode() + timestamp.hashCode();
    }
}
