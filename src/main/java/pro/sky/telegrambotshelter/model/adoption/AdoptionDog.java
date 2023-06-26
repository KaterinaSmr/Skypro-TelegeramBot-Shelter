package pro.sky.telegrambotshelter.model.adoption;

import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.model.Pet;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AdoptionDog extends Adoption<PersonDog> {
    public AdoptionDog() {
    }

    public AdoptionDog(PersonDog person, Pet pet, LocalDate probationStartDate, LocalDate probationEndDate, AdoptionStatus adoptionStatus) {
        super(person, pet,probationStartDate, probationEndDate, adoptionStatus);
    }

}
