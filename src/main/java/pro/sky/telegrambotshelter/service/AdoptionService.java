package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.PersonNotFoundException;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.AdoptionRepository;

import java.util.Collection;

@Service
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final PersonService personService;
    private final PetService petService;

    public AdoptionService(AdoptionRepository adoptionRepository, PersonService personService, PetService petService) {
        this.adoptionRepository = adoptionRepository;
        this.personService = personService;
        this.petService = petService;
    }

    public Collection<Adoption> getAllAdoptions(){
        return adoptionRepository.findAll();
    }

    public Adoption findById(int id){
        return adoptionRepository.findById(id).orElse(null);
    }

    public Adoption findByChatId(long chatId){
        Person person = personService.findPersonByChatId(chatId)
                .orElseThrow(PersonNotFoundException::new);
        return adoptionRepository.findByPerson(person);
    }

    public Adoption getByPetId(int petId) {
        Pet pet = petService.get(petId);
        if (pet == null) {
            return null;
        }
        return adoptionRepository.findByPet(pet);
    }

    public Adoption getByPersonId(Integer personId) {
        Person person = personService.get(personId);
        if (person == null) {
            return null;
        }
        return adoptionRepository.findByPerson(person);
    }

    public Adoption save(Adoption adoption){
        if (getByPersonId(adoption.getPerson().getId()) != null || getByPetId(adoption.getPet().getId()) != null)
            return null;
        return adoptionRepository.save(adoption);
    }

    public Adoption edit(Adoption adoption) {
        Adoption adoptionFound = findById(adoption.getId());
        if (adoption == null){
            return null;
        }
        return adoptionRepository.save(adoption);
    }

    public void delete(Integer id) {
        adoptionRepository.deleteById(id);
    }
}
