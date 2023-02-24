package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptionDog;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.repository.AdoptionDogRepository;

@Service
public class AdoptionDogService extends AdoptionService<AdoptionDog, PersonDog>{
    public AdoptionDogService(AdoptionDogRepository adoptionRepository, PersonDogService personService,
                              PetService petService) {
        super(adoptionRepository, personService, petService);
    }
}
