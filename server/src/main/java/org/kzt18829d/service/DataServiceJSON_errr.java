package org.kzt18829d.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.kzt18829d.core.domain.Account;
import org.kzt18829d.core.domain.BankAccount;
import org.kzt18829d.core.domain.Transaction;
import org.kzt18829d.core.ports.DataService;
import org.kzt18829d.core.type.BankAccountID;
import org.kzt18829d.core.type.TransactionID;
import org.kzt18829d.exception.DataServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataServiceJSON_errr implements DataService {
    private static final Logger log = LoggerFactory.getLogger(DataServiceJSON_errr.class);
    private final ObjectMapper objectMapper;
    private static Path defaultFilePath;
    private static final TypeReference<Account> accountReference = new TypeReference<Account>() {};
    private static final TypeReference<BankAccount> bankAccountTypeReference = new TypeReference<BankAccount>() {};
    private static final TypeReference<Transaction> transactionTypeReference = new TypeReference<Transaction> () {};
    private static final TypeReference<Map<UUID, Account>> accountTypeReferenceMap = new TypeReference<Map<UUID, Account>>() {};
    private static final TypeReference<Map<BankAccountID, BankAccount>> bankAccountTypeReferenceMap = new TypeReference<Map<BankAccountID, BankAccount>>() {};
    private static final TypeReference<Map<TransactionID, Transaction>> transactionTypeReferenceMap = new TypeReference<Map<TransactionID, Transaction>>() {};
    private static final class InstanceHolder {
        private static final DataServiceJSON_errr instance = new DataServiceJSON_errr();
    }

    private DataServiceJSON_errr() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Метод статический, поэтому синхронизация идет по самому классу
    public static synchronized void setDefaultFilePath(Path path) {
        DataServiceJSON_errr.defaultFilePath = path;
    }

    @Override
    public boolean canUseFileDirectory() {
        return true;
    }

    @Override
    public String DataServiceType() {
        return "JSON";
    }

    private File checkFile(Path path) {
        if (path == null || path.toString().isBlank()) {
            throw new DataServiceException("FilePath is null or empty. Set new path and repeat");
        }

        File file = new File(path.toString());

        if (!file.exists()) {
            throw new DataServiceException("File exits error");
        }

        return file;
    }

    private void checkExist(File file) throws FileNotFoundException {
        if (!file.exists()) {
            log.error("File not exist");
            throw new FileNotFoundException("File not exist");
        }
    }

    @Override
    public synchronized void saveAccount(Account account) throws IOException {
        saveAccount(account, defaultFilePath);
    }

    @Override
    public synchronized void saveBankAccount(BankAccount bankAccount) throws IOException {
        saveBankAccount(bankAccount, defaultFilePath);
    }

    @Override
    public synchronized void saveTransaction(Transaction transaction) throws IOException {
        saveTransaction(transaction, defaultFilePath);
    }

    @Override
    public synchronized void saveAccounts(Map<UUID, Account> accountMap) throws IOException {
        saveAccounts(accountMap, defaultFilePath);
    }

    @Override
    public synchronized void saveBankAccounts(Map<BankAccountID, BankAccount> bankAccountMap) throws IOException {
        saveBankAccounts(bankAccountMap, defaultFilePath);
    }

    @Override
    public synchronized void saveTransactions(Map<TransactionID, Transaction> transactionMap) throws IOException {
        saveTransactions(transactionMap, defaultFilePath);
    }

    @Override
    public synchronized void saveAccounts(List<Account> accountList) throws IOException {
        saveAccounts(accountList, defaultFilePath);
    }

    @Override
    public synchronized void saveBankAccounts(List<BankAccount> bankAccountList) throws IOException {
        saveBankAccounts(bankAccountList, defaultFilePath);
    }

    @Override
    public synchronized void saveTransactions(List<Transaction> transactionList) throws IOException {
        saveTransactions(transactionList, defaultFilePath);
    }

    // Эти методы тоже стоит сделать synchronized, т.к. они, вероятно, будут читать из defaultFilePath
    @Override
    public synchronized Account loadAccount() throws IOException {
        return loadAccount(defaultFilePath);
    }

    @Override
    public synchronized BankAccount loadBankAccount() throws IOException {
        return loadBankAccount(defaultFilePath);
    }

    @Override
    public synchronized Transaction loadTransaction() throws IOException {
        return loadTransaction(defaultFilePath);
    }

    @Override
    public synchronized Map<UUID, Account> loadAccounts() throws IOException {
        return loadAccounts(defaultFilePath);
    }

    @Override
    public synchronized Map<BankAccountID, BankAccount> loadBankAccounts() throws IOException {
        return loadBankAccounts(defaultFilePath);
    }

    @Override
    public synchronized Map<TransactionID, Transaction> loadTransactions() throws IOException {
        return loadTransactions(defaultFilePath);
    }

    @Override
    public synchronized List<Account> loadAccountsList() throws IOException {
        return loadAccountsList(defaultFilePath);
    }

    @Override
    public synchronized List<BankAccount> loadBankAccountsList() throws IOException {
        return loadBankAccountsList(defaultFilePath);
    }

    @Override
    public synchronized List<Transaction> loadTransactionsList() throws IOException {
        return loadTransactionsList(defaultFilePath);
    }

    // Все методы, которые непосредственно работают с файлами, должны быть synchronized
    @Override
    public synchronized void saveAccount(Account account, Path filePath) throws IOException {
        log.info("Saving account data...");
        if (account == null) {
            log.error("Save process failed. Account cannot be null");
            throw new NullPointerException("Account cannot be null");
        }

        try {
            File file = checkFile(filePath);
            objectMapper.writeValue(file, account);
        } catch (IOException e) {
            log.error("Save process failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized void saveBankAccount(BankAccount bankAccount, Path filePath) throws IOException {
        log.info("Saving bankAccount data...");
        if (bankAccount == null) {
            log.error("Save process failed. BankAccount cannot be null");
            throw new NullPointerException("BankAccount cannot be null");
        }

        try {
            File file = checkFile(filePath);
            objectMapper.writeValue(file, bankAccount);
        } catch (IOException e) {
            log.error("Save process failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized void saveTransaction(Transaction transaction, Path filePath) throws IOException {
        log.info("Saving transaction data...");
        if (transaction == null) {
            log.error("Save process failed. Transaction cannot be null");
            throw new NullPointerException("Transaction cannot be null");
        }

        try {
            File file = checkFile(filePath);
            objectMapper.writeValue(file, transaction);
        } catch (IOException e) {
            log.error("ave process failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized void saveAccounts(Map<UUID, Account> accountMap, Path filePath) throws IOException {
        log.info("Saving Accounts data from map...");
        if (accountMap == null){
            log.error("Save process failed. AccountMap cannot be null");
            throw new DataServiceException("AccountMap cannot be null");
        }

        try {
            File file = checkFile(filePath);
            objectMapper.writeValue(file, accountMap);
        } catch (Exception e) {
            log.error("Save process failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized void saveBankAccounts(Map<BankAccountID, BankAccount> bankAccountMap, Path filePath) throws IOException {
        log.info("Saving BankAccounts data from map...");
        if (bankAccountMap == null){
            log.error("Save process failed. BankAccountMap cannot be null");
            throw new DataServiceException("BankAccountMap cannot be null");
        }

        try {
            File file = checkFile(filePath);
            objectMapper.writeValue(file, bankAccountMap);
        } catch (Exception e) {
            log.error("Save process failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized void saveTransactions(Map<TransactionID, Transaction> transactionMap, Path filePath) throws IOException {
        log.info("Saving Transactions data from map...");
        if (transactionMap == null){
            log.error("Save process failed. TransactionMap cannot be null");
            throw new DataServiceException("TransactionMap cannot be null");
        }

        try {
            File file = checkFile(filePath);
            objectMapper.writeValue(file, transactionMap);
        } catch (Exception e) {
            log.error("Save process failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized void saveAccounts(List<Account> accountList, Path filePath) throws IOException {
        saveAccounts(accountList.stream().collect(Collectors.toMap(Account::getAccountUUID, t -> t)), filePath);
    }

    @Override
    public synchronized void saveBankAccounts(List<BankAccount> bankAccountList, Path filePath) throws IOException {
        saveBankAccounts(bankAccountList.stream().collect(Collectors.toMap(BankAccount::getAccountID, t -> t)), filePath);
    }

    @Override
    public synchronized void saveTransactions(List<Transaction> transactionList, Path filePath) throws IOException {
        saveTransactions(transactionList.stream().collect(Collectors.toMap(Transaction::getTransactionID, t -> t)), filePath);
    }

    @Override
    public synchronized Account loadAccount(Path filePath) throws IOException {
        try {
            File file = checkFile(filePath);
            checkExist(file);

            return objectMapper.readValue(file, accountReference);
        } catch (Exception e) {
            log.error("Load process exception. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized BankAccount loadBankAccount(Path filePath) throws IOException {
        try {
            File file = checkFile(filePath);
            checkExist(file);

            return objectMapper.readValue(file, bankAccountTypeReference);
        } catch (Exception e) {
            log.error("Load process exception. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized Transaction loadTransaction(Path filePath) throws IOException {
        try {
            File file = checkFile(filePath);
            checkExist(file);

            return objectMapper.readValue(file, transactionTypeReference);
        } catch (Exception e) {
            log.error("Load process exception. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized Map<UUID, Account> loadAccounts(Path filePath) throws IOException {
        try {
            File file = checkFile(filePath);
            checkExist(file);
            return objectMapper.readValue(file, accountTypeReferenceMap);
        } catch (Exception e) {
            log.error("Load process exception. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized Map<BankAccountID, BankAccount> loadBankAccounts(Path filePath) throws IOException {
        try {
            File file = checkFile(filePath);
            checkExist(file);
            return objectMapper.readValue(file, bankAccountTypeReferenceMap);
        } catch (Exception e) {
            log.error("Load process exception. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized Map<TransactionID, Transaction> loadTransactions(Path filePath) throws IOException {
        try {
            File file = checkFile(filePath);
            checkExist(file);
            return objectMapper.readValue(file, transactionTypeReferenceMap);
        } catch (Exception e) {
            log.error("Load process exception. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized List<Account> loadAccountsList(Path filePath) throws IOException {
        return loadAccounts(filePath).values().stream().toList();
    }

    @Override
    public synchronized List<BankAccount> loadBankAccountsList(Path filePath) throws IOException {
        return loadBankAccounts(filePath).values().stream().toList();
    }

    @Override
    public synchronized List<Transaction> loadTransactionsList(Path filePath) throws IOException {
        return loadTransactions(filePath).values().stream().toList();
    }
}