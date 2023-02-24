package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AdoptionReportDog extends AdoptionReport<AdoptionDog, PersonDog>{

    public AdoptionReportDog() {
    }

    public AdoptionReportDog(AdoptionDog adoption, String filePath, String mediaType, LocalDate date) {
        super(adoption, filePath, mediaType, date);
    }
}
