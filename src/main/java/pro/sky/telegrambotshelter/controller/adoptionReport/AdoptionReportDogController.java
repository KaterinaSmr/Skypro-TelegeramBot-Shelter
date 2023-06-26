package pro.sky.telegrambotshelter.controller.adoptionReport;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportDog;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;

@RestController
@RequestMapping("/adoption_report_dog")
public class AdoptionReportDogController extends AdoptionReportController<AdoptionReportDog, AdoptionDog, PersonDog>{

    public AdoptionReportDogController(AdoptionReportDogService adoptionReportService) {
        super(adoptionReportService);
    }
}
