package org.kzt18829d.repository;

import org.kzt18829d.core.domain.Transaction;
import org.kzt18829d.core.ports.Repository;
import org.kzt18829d.core.type.BankAccountID;
import org.kzt18829d.core.type.TransactionID;
import org.kzt18829d.exception.RepositoryException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionRepository implements Repository<TransactionID, Transaction> {
    private final Map<TransactionID, Transaction> repository = new ConcurrentHashMap<>();

    public TransactionRepository() {}

    public TransactionRepository(Map<TransactionID, Transaction> newRepository) {
        if (newRepository != null) {
            repository.putAll(newRepository);
        }
    }

    @Override
    public void uploadRepository(Map<TransactionID, Transaction> newRepository) {
        Objects.requireNonNull(newRepository, "New repository cannot be null");

        repository.clear();
        repository.putAll(newRepository);
    }

    @Override
    public Map<TransactionID, Transaction> downloadRepository() {
        return repository.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean contains(TransactionID objectID) {
        return repository.containsKey(objectID);
    }

    @Override
    public void add(Transaction object) {
        Objects.requireNonNull(object, "Transaction cannot be null");

        if (repository.putIfAbsent(object.getTransactionID(), object) != null)
            throw new RepositoryException("Transaction was added earlier");
    }

    @Override
    public Transaction remove(TransactionID objectID) {
        Objects.requireNonNull(objectID, "TransactionID cannot be null");
        Transaction t = repository.remove(objectID);
        if (t == null) throw new RepositoryException("Transaction not found");
        return t;
    }

    @Override
    public List<Transaction> getAll() {
        return repository.values().stream().toList();
    }

    public Optional<Transaction> getTransaction(TransactionID transactionID) {
        return Optional.ofNullable(repository.get(transactionID));
    }

    public List<Transaction> getBySender(BankAccountID bankAccountID) {
        return getBySender(bankAccountID.getID());
    }

    public List<Transaction> getByReceiver(BankAccountID bankAccountID) {
        return getByReceiver(bankAccountID.getID());
    }

    public List<Transaction> getBySender(String bankAccountID) {
        return repository.values().stream().filter(transaction -> transaction.getSender().equals(bankAccountID)).toList();
    }


    public List<Transaction> getByReceiver(String bankAccountID) {
        return repository.values().stream().filter(transaction -> transaction.getReceiver().equals(bankAccountID)).toList();
    }
}
