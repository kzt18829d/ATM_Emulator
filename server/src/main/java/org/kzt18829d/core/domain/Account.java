package org.kzt18829d.core.domain;

import org.kzt18829d.core.type.OwnerName;

import java.time.LocalDate;
import java.util.UUID;

public class Account {
    private final UUID accountUUID;
    private final OwnerName ownerName;
    private final LocalDate registryDate;

    public Account(OwnerName ownerName) {
        this.accountUUID = UUID.randomUUID();
        this.ownerName = ownerName;
        this.registryDate = LocalDate.now();
    }

    public Account(UUID accountUUID, OwnerName ownerName, LocalDate registryDate) {
        this.accountUUID = accountUUID;
        this.ownerName = ownerName;
        this.registryDate = registryDate;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public OwnerName getUserName() {
        return ownerName;
    }

    public LocalDate getRegistryDate() {
        return registryDate;
    }
}
