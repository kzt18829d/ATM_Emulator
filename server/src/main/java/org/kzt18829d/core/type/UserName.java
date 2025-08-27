package org.kzt18829d.core.type;

import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonCreator
    public UserName(
            @JsonProperty("surname") String surname,
            @JsonProperty("name") String name,
            @JsonProperty("patronymic") String patronymic) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    @JsonGetter("surname")
    public String getUserSurname() {
        return surname;
    }

    @JsonGetter("name")
    public String getUserName() {
        return name;
    }

    @JsonGetter("patronymic")
    public String getUserPatronymic() {
        return patronymic;
    }

    @JsonIgnore
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
