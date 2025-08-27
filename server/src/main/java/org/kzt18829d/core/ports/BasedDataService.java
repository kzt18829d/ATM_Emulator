package org.kzt18829d.core.ports;

import org.kzt18829d.exception.DataServiceException;

import java.util.Collection;

public interface BasedDataService<T> {
    void save(T object) throws DataServiceException;
    void save(Collection<T> collection) throws DataServiceException;
    T loadObject() throws DataServiceException;
    Collection<T> load() throws DataServiceException;
}
