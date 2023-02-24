package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptionCat;
import pro.sky.telegrambotshelter.model.PersonCat;
import pro.sky.telegrambotshelter.repository.AdoptionCatRepository;

@Service
public class AdoptionCatService extends AdoptionService<AdoptionCat, PersonCat>{
    public AdoptionCatService(AdoptionCatRepository adoptionRepository, PersonCatService personService,
                              PetService petService) {
        super(adoptionRepository, personService, petService);
    }
}
