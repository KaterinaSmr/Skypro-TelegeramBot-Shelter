package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.PersonNotFoundException;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.AdoptionRepository;

import java.time.LocalDate;
import java.util.Collection;

/**
 * A Service class to perform CRUD operations with the "adoption" table in database.
 * @author Ekaterina Gorbacheva
 */
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

//    public Collection<Adoption> getAdoptionsWithActiveProbation(){
//        return adoptionRepository.findAllByProbationFinished(false);
//    }

    /**
     * A method to get an {@link Adoption} object for a paticular {@link Person} object from "adoption" table in db.
     * Uses {@link AdoptionRepository}
     * @param personId identification of a {@link Person}
     * @return {@link Adoption} object if found, null if {@link Person} with this id does not exist,
     * or {@link Adoption} record for this person is not found
     * @see PersonService
     */
    public Adoption getByPersonId(int personId) {
        Person person = personService.get(personId);
        if (person == null) {
            return null;
        }
        return adoptionRepository.findByPerson(person);
    }

    /**
     * A method to save a new {@link Adoption} record to the "adoption" table. Uses {@link AdoptionRepository}.
     * The new {@link Adoption} object must contain unique personId and petId values. If the "adoption" table
     * already contains a record with the same personId or petId, then the new {@link Adoption} object is not saved
     * to the db table.
     * @param adoption {@link Adoption} object to be saved
     * @return newly saved {@link Adoption} object, or null in case of duplicated personId or petId
     * @see PersonService
     * @see PetService
     */
    public Adoption save(Adoption adoption){
        if (getByPersonId(adoption.getPerson().getId()) != null || getByPetId(adoption.getPet().getId()) != null)
            return null;
        return adoptionRepository.save(adoption);
    }

    public Adoption edit(Adoption adoption) {
        Adoption adoptionFound = findById(adoption.getId());
        if (adoptionFound == null){
            return null;
        }
        return adoptionRepository.save(adoption);
    }

    public void delete(Integer id) {
        adoptionRepository.deleteById(id);
    }

    public Adoption setNewProbationEndDate(int adoptionId, LocalDate newDate) {
        Adoption adoptionFound = findById(adoptionId);
        System.out.println("Adoption found by id: " + adoptionFound);
        if (adoptionFound != null) {
            adoptionFound.setProbationEndDate(newDate);
            System.out.println("Adoption after date update "  + adoptionFound );
        }
        return adoptionRepository.save(adoptionFound);
    }
}
