package pro.sky.telegrambotshelter.controller.adoption;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;

@RestController
@RequestMapping("/adoption_cat")
public class AdoptionCatController extends AdoptionController<AdoptionCat, PersonCat> {

    public AdoptionCatController(AdoptionCatService adoptionService) {
        this.adoptionService = adoptionService;
    }
}
