package pro.sky.telegrambotshelter.service.adoption;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.repository.AdoptionDogRepository;
import pro.sky.telegrambotshelter.service.person.PersonDogService;
import pro.sky.telegrambotshelter.service.PetService;

@Service
public class AdoptionDogService extends AdoptionService<AdoptionDog, PersonDog>{
    public AdoptionDogService(AdoptionDogRepository adoptionRepository, PersonDogService personService,
                              PetService petService) {
        super(adoptionRepository, personService, petService);
    }
}
