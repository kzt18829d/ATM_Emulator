package org.kzt18829d.core.ports;

import org.kzt18829d.core.currency.CurrencyType;
import org.kzt18829d.core.domain.BankAccount;
import org.kzt18829d.core.identificators.BankAccountID;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface BankAccountService {

    BankAccount createAccount(UUID ownerUUID, CurrencyType currencyType, BigDecimal currencyValue);

    BankAccount createAccount(UUID ownerUUID, CurrencyType currencyType);

    BankAccount removeBankAccount(BankAccountID bankAccountID);

    Optional<BankAccount> findBankAccount(BankAccountID bankAccountID);

    List<BankAccount> findBankAccount(UUID ownerUUID);

    <T, V> List<BankAccount> findBankAccount(Function<T, V> function);

}
