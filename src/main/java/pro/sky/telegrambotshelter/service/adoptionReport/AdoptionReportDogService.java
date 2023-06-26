package pro.sky.telegrambotshelter.service.adoptionReport;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportDog;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.repository.AdoptionReportDogRepository;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;

@Service
public class AdoptionReportDogService extends AdoptionReportService<AdoptionReportDog, AdoptionDog, PersonDog> {
    public AdoptionReportDogService(AdoptionReportDogRepository adoptionReportRepository, AdoptionDogService adoptionService) {
        super(adoptionReportRepository, adoptionService);
    }
}
