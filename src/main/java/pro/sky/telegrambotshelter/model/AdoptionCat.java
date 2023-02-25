package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AdoptionCat extends Adoption<PersonCat> {

    public AdoptionCat() {
    }

    public AdoptionCat(PersonCat person, Pet pet, LocalDate probationStartDate, LocalDate probationEndDate, AdoptionStatus adoptionStatus) {
        super(person, pet,probationStartDate, probationEndDate, adoptionStatus);
    }

}
