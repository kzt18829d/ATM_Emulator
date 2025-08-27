package org.kzt18829d.service;

import org.kzt18829d.core.ports.DataService;
import org.kzt18829d.core.ports.FileDataService;
import org.kzt18829d.exception.DataServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonDataService implements DataService, FileDataService {
    private static final Logger log = LoggerFactory.getLogger(JsonDataService.class);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    @Override
    public boolean canUseFileDirectory() {
        return true;
    }

    @Override
    public String getDataServiceType() {
        return "JSON";
    }

    @Override
    public void setWorkingDirectory(Path directory) throws DataServiceException {

    }

    @Override
    public Path getWorkingDirectory() {
        return null;
    }
}
