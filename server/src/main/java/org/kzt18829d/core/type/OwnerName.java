package org.kzt18829d.core.type;

import org.kzt18829d.exception.IdentificatorException;

import java.util.Objects;

public class OwnerName {
    private final String name;
    private final String surname;
    private final String patronymic;

    public OwnerName(String name, String surname, String patronymic) {
        if (name == null || name.isBlank())
            throw new IdentificatorException("Name cannot be null or empty");
        if (surname == null || surname.isBlank())
            throw new IdentificatorException("Surname cannot be null or empty");
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OwnerName ownerName = (OwnerName) o;
        return Objects.equals(name, ownerName.name) && Objects.equals(surname, ownerName.surname) && Objects.equals(patronymic, ownerName.patronymic);
    }

    @Override
    public int hashCode() {
        return 23 * name.hashCode() + surname.hashCode() + patronymic.hashCode();
    }
}
