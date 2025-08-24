package org.kzt18829d.core.domain;

import org.kzt18829d.core.type.UserName;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Account {
    private final UUID accountUUID;
    private final UserName userName;
//    private final Set<BankAccount> bankAccounts;
    private final LocalDate registryDate;

    public Account(UserName userName /*, BankAccount bankAccount*/) {
        this.accountUUID = UUID.randomUUID();
        this.userName = userName;
//        this.bankAccounts = new HashSet<>();
//        if (!(bankAccount == null)) this.bankAccounts.add(bankAccount);
        this.registryDate = LocalDate.now();
    }

    public Account(UUID accountUUID, UserName userName /*, Set<BankAccount> bankAccounts*/, LocalDate registryDate) {
        this.accountUUID = accountUUID;
        this.userName = userName;
//        this.bankAccounts = bankAccounts;
        this.registryDate = registryDate;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public UserName getUserName() {
        return userName;
    }

    public LocalDate getRegistryDate() {
        return registryDate;
    }

//    public Set<BankAccount> getBankAccounts() {
//        return bankAccounts;
//    }



//    public void addBankAccount(BankAccount bankAccount) {
//        this.bankAccounts.add(bankAccount);
//    }

//    public void removeBankAccount(BankAccount bankAccount) {
//        this.bankAccounts.remove(bankAccount);
//    }
}
