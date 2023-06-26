package pro.sky.telegrambotshelter.controller.adoption;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;

@RestController
@RequestMapping("/adoption_dog")
public class AdoptionDogController extends AdoptionController<AdoptionDog, PersonDog> {

    public AdoptionDogController(AdoptionDogService adoptionService) {
        this.adoptionService = adoptionService;
    }
}
