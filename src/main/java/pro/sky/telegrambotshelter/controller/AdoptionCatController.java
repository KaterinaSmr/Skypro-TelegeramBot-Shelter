package pro.sky.telegrambotshelter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.AdoptionCat;
import pro.sky.telegrambotshelter.model.PersonCat;
import pro.sky.telegrambotshelter.service.AdoptionCatService;

@RestController
@RequestMapping("/adoption_cat")
public class AdoptionCatController extends AdoptionController<AdoptionCat, PersonCat> {

    public AdoptionCatController(AdoptionCatService adoptionService) {
        this.adoptionService = adoptionService;
    }
}
