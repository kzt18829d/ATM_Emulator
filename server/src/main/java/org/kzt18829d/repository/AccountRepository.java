package org.kzt18829d.repository;

import org.kzt18829d.core.domain.Account;
import org.kzt18829d.core.ports.Repository;
import org.kzt18829d.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class AccountRepository implements Repository<UUID, Account> {
    private final Logger log = LoggerFactory.getLogger(AccountRepository.class);
    private final ConcurrentHashMap<UUID, Account> repository = new ConcurrentHashMap<>();

    public AccountRepository() {}

    public AccountRepository(Map<UUID, Account> newRepository) {
        if (newRepository != null) {
            this.repository.putAll(newRepository);
        }
    }

    @Override
    public void uploadRepository(Map<UUID, Account> newRepository) {
        Objects.requireNonNull(newRepository, "New repository cannot be null");

        if (newRepository.isEmpty()) {
            log.warn("Repository upload warn. Upload empty repository");
        }

        repository.clear();
        repository.putAll(newRepository);

        log.info("Repository upload success");
    }

    @Override
    public Map<UUID, Account> downloadRepository() {
        return repository.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean contains(UUID objectID) {
        return repository.containsKey(objectID);
    }

    public void add(Account account) {
        Objects.requireNonNull(account, "Account cannot be null");
        UUID uuid = account.getAccountUUID();

        // putIfAbsent возвращает null, если элемент был добавлен, иначе - существующий элемент
        if (repository.putIfAbsent(uuid, account) != null) {
            throw new RepositoryException("Account was added earlier");
        }

        log.trace("Repository add new account: {}", uuid);
    }

    public Account remove(UUID accountUUID) {
        Objects.requireNonNull(accountUUID, "UUID cannot be null");

        Account removedAccount = repository.remove(accountUUID);
        if (removedAccount == null) {
            throw new RepositoryException("Account not found");
        }

        log.trace("Repository remove account: {}", accountUUID);
        return removedAccount;
    }

    public Optional<Account> getByUUID(UUID accountUUID) {
        return Optional.ofNullable(repository.get(accountUUID));
    }

    public List<Account> getByName(String substring) {
        return repository.values().stream().filter(value -> value.getUserName().getFullUserName().contains(substring)).toList();
    }

    public List<Account> getAll() {
        return repository.values().stream().toList();
    }

}
