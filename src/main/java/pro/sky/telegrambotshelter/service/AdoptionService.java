package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.PersonNotFoundException;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionStatus;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.AdoptionRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

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

    /**
     * Finds all {@link Adoption} object records in the "adoption" db table with the active probation status.
     * Active probation status is determined by the values in the adoption_status column. Active adoption status
     * corresponds to {@link AdoptionStatus#ON_PROBATION} or {@link AdoptionStatus#PROBATION_EXTENDED} values in
     * the adoption_status field
     * Uses {@link AdoptionRepository}.
     * @return {@link List} of {@link Adoption} objects
     */
    public List<Adoption> getAllActiveProbations(){
        return getAllAdoptionsByStatus(AdoptionStatus.ON_PROBATION, AdoptionStatus.PROBATION_EXTENDED);
    }

    /**
     * Finds all {@link Adoption} object records having adoption_status values withing the values in parameters
     * @param adoptionStatuses - array of {@link AdoptionStatus} to be included in selection
     * @return {@link List} of {@link Adoption} objects
     */
    public List<Adoption> getAllAdoptionsByStatus(AdoptionStatus ... adoptionStatuses){
        return adoptionRepository.findAllByAdoptionStatusIn(adoptionStatuses);
    }

    public Adoption findById(int id){
        return adoptionRepository.findById(id).orElse(null);
    }

    public Adoption findByChatId(long chatId){
        Person person = personService.findPersonByChatId(chatId)
                .orElseThrow(PersonNotFoundException::new);
        return adoptionRepository.findByPerson(person);
    }

    public Adoption findByPetId(int petId) {
        Pet pet = petService.findById(petId);
        if (pet == null) {
            return null;
        }
        return adoptionRepository.findByPet(pet);
    }

    /**
     * A method to get an {@link Adoption} object for a particular {@link Person} object from "adoption" table in db.
     * Uses {@link AdoptionRepository}
     * @param personId identification of a {@link Person}
     * @return {@link Adoption} object if found, null if {@link Person} with this id does not exist,
     * or {@link Adoption} record for this person is not found
     * @see PersonService
     */
    public Adoption findByPersonId(int personId) {
        Person person = personService.findById(personId);
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
        if (findByPersonId(adoption.getPerson().getId()) != null || findByPetId(adoption.getPet().getId()) != null)
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

    /**
     * Removes a record from the "adoption" table with this id. Uses {@link AdoptionRepository}
     * @param id identification of a {@link Adoption} to be removed from db table "adoption"
     */
    public void delete(Integer id) {
        adoptionRepository.deleteById(id);
    }

    public Adoption setNewStatus(int adoptionId, AdoptionStatus adoptionStatus){
        Adoption adoption = findById(adoptionId);
        return setNewStatus(adoption, adoptionStatus);
    }

    public Adoption setNewStatus(Adoption adoption, AdoptionStatus adoptionStatus){
        if (adoption != null){
            adoption.setAdoptionStatus(adoptionStatus);
            adoptionRepository.save(adoption);
        }
        return adoption;
    }

    public Adoption setNewProbationEndDate(int adoptionId, LocalDate newDate) {
        Adoption adoption = findById(adoptionId);
        return setNewProbationEndDate(adoption, newDate);
    }
    public Adoption setNewProbationEndDate(Adoption adoption, LocalDate newDate) {
        if (adoption != null) {
            adoption.setProbationEndDate(newDate);
            adoptionRepository.save(adoption);
        }
        return adoption;
    }
}
