package org.kzt18829d.core.ports;

import org.kzt18829d.core.domain.Account;
import org.kzt18829d.core.type.OwnerName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    Account createAccount(OwnerName ownerName);

    Account removeAccount(UUID accountUUID);

    Optional<Account> findAccount(UUID accountUUID);

    Optional<Account> findAccount(OwnerName ownerName);

    Optional<Account> findAccount(String substring);

    List<Account> findAccounts(String substring);

    List<Account> findAccounts(OwnerName ownerName);


}
