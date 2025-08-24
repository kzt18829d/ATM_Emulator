package org.kzt18829d.core.type;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserName {
    private final String surname;
    private final String name;
    private final String patronymic;

    public UserName(String fullName) {
        List<String> nameParts = Arrays.stream(fullName.split("\\s+")).toList();
        this.surname = nameParts.get(0);
        this.name = nameParts.get(1);
        this.patronymic = nameParts.get(2);
    }

    public UserName(String surname, String name, String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }


    public String getUserSurname() {
        return surname;
    }

    public String getUserName() {
        return name;
    }

    public String getUserPatronymic() {
        return patronymic;
    }

    public String getFullUserName() {
        return String.join(" ", surname, name, patronymic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserName userName = (UserName) o;
        return Objects.equals(surname, userName.surname) && Objects.equals(name, userName.name) && Objects.equals(patronymic, userName.patronymic);
    }

    @Override
    public int hashCode() {
        return 27 * surname.hashCode() + name.hashCode() + surname.hashCode();
    }
}
