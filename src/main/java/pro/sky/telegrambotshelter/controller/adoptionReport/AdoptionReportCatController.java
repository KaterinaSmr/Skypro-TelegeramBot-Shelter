package pro.sky.telegrambotshelter.controller.adoptionReport;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportCat;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;

@RestController
@RequestMapping("/adoption_report_cat")
public class AdoptionReportCatController extends AdoptionReportController<AdoptionReportCat, AdoptionCat, PersonCat>{

    public AdoptionReportCatController(AdoptionReportCatService adoptionReportService) {
        super(adoptionReportService);
    }
}
