package pro.sky.telegrambotshelter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.AdoptionDog;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.service.AdoptionDogService;

@RestController
@RequestMapping("/adoption_dog")
public class AdoptionDogController extends AdoptionController<AdoptionDog, PersonDog> {

    public AdoptionDogController(AdoptionDogService adoptionService) {
        this.adoptionService = adoptionService;
    }
}
