package org.kzt18829d.core.ports;

import org.kzt18829d.exception.DataServiceException;

import java.nio.file.Path;
import java.util.Collection;

public interface FileDataService {
    void setWorkingDirectory(Path directory) throws DataServiceException;
    Path getWorkingDirectory();

//    void save(T object, Path filePath) throws DataServiceException;
//    void save(Collection<T> collection, Path filePath) throws DataServiceException;
//    T loadObject(Path filePath) throws DataServiceException;
//    Collection<T> load(Path filePath) throws DataServiceException;
}
