package pro.sky.telegrambotshelter.model.person;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonCat extends Person {

    public PersonCat() {
    }

    public PersonCat(long chatId, String firstName, String lastName, String phone, String email) {
        super(chatId, firstName, lastName, phone, email);
    }
}
