package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonDog extends Person {

    public PersonDog() {
    }

    public PersonDog(long chatId, String firstName, String lastName, String phone, String email) {
        super(chatId, firstName, lastName, phone, email);
    }
}
