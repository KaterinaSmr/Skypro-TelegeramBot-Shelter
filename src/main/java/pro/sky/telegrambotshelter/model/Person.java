package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@MappedSuperclass
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long chatId;
    @NotEmpty(message = "Имя не должно быть пустым")
    private String firstName;
    @NotEmpty(message = "Имя не должно быть пустым")
    private String lastName;
    @NotEmpty(message = "Номер телефона не должен быть пустым")
    @Pattern(regexp="(\\+[\\d]{11})", message = "Номер телефона должен состоять из кода страны (+7) и 10 цифр")
    private String phone;
    @NotEmpty(message = "Адрес электронной почты не должен быть пустым")
    @Email(message = "Должен быть указан корректный адрес электронной почты")
    private String email;

    public Person() {
    }

    public Person(long chatId) {
        this.chatId = chatId;
    }

    public Person(long chatId, String firstName, String lastName, String phone, String email) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
