package org.kzt18829d.core.ports;

import java.util.List;
import java.util.Map;

public interface Repository<ID, T> {
    void uploadRepository(Map<ID, T> newRepository);
    Map<ID, T> downloadRepository();

    boolean contains(ID objectID);

    void add(T object);
    T remove(ID objectID);

    List<T> getAll();
}
