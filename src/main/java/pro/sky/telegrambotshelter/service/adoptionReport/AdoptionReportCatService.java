package pro.sky.telegrambotshelter.service.adoptionReport;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportCat;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.repository.AdoptionReportCatRepository;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;

@Service
public class AdoptionReportCatService extends AdoptionReportService<AdoptionReportCat, AdoptionCat, PersonCat> {
    public AdoptionReportCatService(AdoptionReportCatRepository adoptionReportRepository, AdoptionCatService adoptionService) {
        super(adoptionReportRepository, adoptionService);
    }
}
