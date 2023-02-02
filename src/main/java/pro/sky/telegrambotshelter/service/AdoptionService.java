package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.PersonNotFoundException;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.repository.AdoptionRepository;

import java.util.Collection;
import java.util.List;

@Service
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final PersonService personService;

    public AdoptionService(AdoptionRepository adoptionRepository, PersonService personService) {
        this.adoptionRepository = adoptionRepository;
        this.personService = personService;
    }

    public Collection<Adoption> allAdoptions(){
        return adoptionRepository.findAll();
    }

    public Adoption findById(int id){
        return adoptionRepository.findById(id).orElse(null);
    }

    public List<Adoption> findByChatId(long chatId){
        Person person = personService.findPersonByChatId(chatId)
                .orElseThrow(PersonNotFoundException::new);
        return adoptionRepository.findAllByPerson(person);
    }
}
