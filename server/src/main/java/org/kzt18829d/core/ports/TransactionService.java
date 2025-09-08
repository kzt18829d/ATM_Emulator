package org.kzt18829d.core.ports;

import org.kzt18829d.core.domain.BankAccount;
import org.kzt18829d.core.domain.Transaction;
import org.kzt18829d.core.identificators.BankAccountID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction makeTransaction(BankAccountID sender, BankAccountID receiver, BigDecimal amount);

    Optional<Transaction> findTransactionBySender(BankAccountID sender);

    Optional<Transaction> findTransactionByReceiver(BankAccountID receiver);

    List<Transaction> findTransactionsBySender(BankAccountID sender);

    List<Transaction> findTransactionsByReceiver(BankAccountID receiver);

    List<Transaction> getTransactionsByDate(LocalDate date);
}
