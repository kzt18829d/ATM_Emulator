package org.kzt18829d.core.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BankAccountID {
    private final String id;

    @JsonCreator
    public BankAccountID(@JsonProperty("id") String id) {
        this.id = id;
    }

    @JsonGetter("id")
    public String getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountID that = (BankAccountID) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 29 * id.hashCode();
    }
}
