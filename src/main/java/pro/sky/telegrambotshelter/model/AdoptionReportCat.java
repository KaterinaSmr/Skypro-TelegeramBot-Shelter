package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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
