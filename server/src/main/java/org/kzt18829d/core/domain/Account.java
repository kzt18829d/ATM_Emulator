package org.kzt18829d.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.kzt18829d.core.type.UserName;

import java.time.LocalDate;
import java.util.UUID;

public class Account {
    private final UUID accountUUID;
    private final UserName userName;
    private final LocalDate registryDate;

    public Account(UserName userName) {
        this.accountUUID = UUID.randomUUID();
        this.userName = userName;
        this.registryDate = LocalDate.now();
    }

    @JsonCreator
    public Account(
            @JsonProperty("accountUUID") UUID accountUUID,
            @JsonProperty("userName") UserName userName,
            @JsonProperty("registryDate") LocalDate registryDate) {
        this.accountUUID = accountUUID;
        this.userName = userName;
        this.registryDate = registryDate;
    }

    @JsonGetter("accountUUID")
    public UUID getAccountUUID() {
        return accountUUID;
    }

    @JsonGetter("userName")
    public UserName getUserName() {
        return userName;
    }

    @JsonGetter("registryDate")
    public LocalDate getRegistryDate() {
        return registryDate;
    }
}
