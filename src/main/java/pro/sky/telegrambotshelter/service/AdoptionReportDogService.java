package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptionDog;
import pro.sky.telegrambotshelter.model.AdoptionReportDog;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.repository.AdoptionReportDogRepository;

@Service
public class AdoptionReportDogService extends AdoptionReportService<AdoptionReportDog, AdoptionDog, PersonDog> {
    public AdoptionReportDogService(AdoptionReportDogRepository adoptionReportRepository, AdoptionDogService adoptionService) {
        super(adoptionReportRepository, adoptionService);
    }
}
