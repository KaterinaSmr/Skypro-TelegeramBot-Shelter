package pro.sky.telegrambotshelter.model.adoptionReport;

import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AdoptionReportCat extends AdoptionReport<AdoptionCat, PersonCat>{

    public AdoptionReportCat() {
    }

    public AdoptionReportCat(AdoptionCat adoption, String filePath, String mediaType, LocalDate date) {
        super(adoption, filePath, mediaType, date);
    }
}
