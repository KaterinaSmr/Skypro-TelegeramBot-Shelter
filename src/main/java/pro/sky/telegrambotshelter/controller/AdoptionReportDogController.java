package pro.sky.telegrambotshelter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.AdoptionDog;
import pro.sky.telegrambotshelter.model.AdoptionReportDog;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.service.AdoptionReportDogService;

@RestController
@RequestMapping("/adoption_report_dog")
public class AdoptionReportDogController extends AdoptionReportController<AdoptionReportDog, AdoptionDog, PersonDog>{

    public AdoptionReportDogController(AdoptionReportDogService adoptionReportService) {
        super(adoptionReportService);
    }
}
