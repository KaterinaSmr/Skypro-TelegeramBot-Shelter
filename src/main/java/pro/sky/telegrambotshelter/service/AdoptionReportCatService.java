package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptionCat;
import pro.sky.telegrambotshelter.model.AdoptionReportCat;
import pro.sky.telegrambotshelter.model.PersonCat;
import pro.sky.telegrambotshelter.repository.AdoptionReportCatRepository;

@Service
public class AdoptionReportCatService extends AdoptionReportService<AdoptionReportCat, AdoptionCat, PersonCat> {
    public AdoptionReportCatService(AdoptionReportCatRepository adoptionReportRepository, AdoptionCatService adoptionService) {
        super(adoptionReportRepository, adoptionService);
    }
}
