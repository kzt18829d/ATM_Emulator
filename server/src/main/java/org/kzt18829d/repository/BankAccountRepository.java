package org.kzt18829d.repository;

import org.kzt18829d.core.domain.BankAccount;
import org.kzt18829d.core.ports.Repository;
import org.kzt18829d.core.type.BankAccountID;
import org.kzt18829d.core.type.CurrencyType;
import org.kzt18829d.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BankAccountRepository implements Repository<BankAccountID, BankAccount> {
//    private final Logger log = LoggerFactory.getLogger(BankAccountRepository.class);
    private final Map<BankAccountID, BankAccount> repository = new ConcurrentHashMap<>();


    public BankAccountRepository() {}

    public BankAccountRepository(Map<BankAccountID, BankAccount> newRepository) {
        if (newRepository != null) {
            this.repository.putAll(newRepository);
        }
    }

    @Override
    public void uploadRepository(Map<BankAccountID, BankAccount> newRepository) {
        Objects.requireNonNull(newRepository, "New repository cannot be null");

//        if (newRepository.isEmpty()) {
//            log.warn("Repository upload warn. Upload empty repository");
//        }

        repository.clear();
        repository.putAll(newRepository);
    }

    @Override
    public Map<BankAccountID, BankAccount> downloadRepository() {
        return repository.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean contains(BankAccountID objectID) {
        return repository.containsKey(objectID);
    }

    public void add(BankAccount account) {
        Objects.requireNonNull(account, "Account cannot be null");
        BankAccountID accountID = account.getAccountID();
        if (repository.putIfAbsent(accountID, account) != null)
            throw new RepositoryException("Account was added earlier");
    }

    public BankAccount remove(BankAccountID bankAccountID) {
        Objects.requireNonNull(bankAccountID, "Bank account ID cannot be null");
        var account = repository.remove(bankAccountID);
        if (account == null)
            throw new RepositoryException("Bank account not found");
        return account;
    }

    public Optional<BankAccount> getByID(BankAccountID bankAccountID) {
        return Optional.ofNullable(repository.get(bankAccountID));
    }

    public List<BankAccount> getAll() {
        return repository.values().stream().toList();
    }

    public List<BankAccount> getByCurrencyType(CurrencyType currencyType) {
        return repository.values().stream().filter(bankAccount -> bankAccount.getCurrencyType() == currencyType).toList();
    }

    public List<BankAccount> getByOwnerUUID(UUID ownerUUID) {
        return repository.values().stream().filter(bankAccount -> bankAccount.getOwnerID() == ownerUUID).toList();
    }
}
