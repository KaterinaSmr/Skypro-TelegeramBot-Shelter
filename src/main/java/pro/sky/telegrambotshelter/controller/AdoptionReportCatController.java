package pro.sky.telegrambotshelter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.service.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.AdoptionReportDogService;

@RestController
@RequestMapping("/adoption_report_cat")
public class AdoptionReportCatController extends AdoptionReportController<AdoptionReportCat, AdoptionCat, PersonCat>{

    public AdoptionReportCatController(AdoptionReportCatService adoptionReportService) {
        super(adoptionReportService);
    }
}
