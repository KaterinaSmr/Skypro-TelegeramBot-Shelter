package pro.sky.telegrambotshelter.service.adoption;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.repository.AdoptionCatRepository;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.PetService;

@Service
public class AdoptionCatService extends AdoptionService<AdoptionCat, PersonCat>{
    public AdoptionCatService(AdoptionCatRepository adoptionRepository, PersonCatService personService,
                              PetService petService) {
        super(adoptionRepository, personService, petService);
    }
}
