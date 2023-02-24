package pro.sky.telegrambotshelter.service;

import pro.sky.telegrambotshelter.PersonNotFoundException;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionStatus;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.AdoptionRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

/**
 * A Service class to perform CRUD operations with the "adoption" table in database.
 * @author Ekaterina Gorbacheva
 */

public abstract class AdoptionService <S extends Adoption<T>, T extends Person> {
    private final AdoptionRepository<S, T> adoptionRepository;
    private final PersonService<T> personService;
    private final PetService petService;
    private final DateTimeFormatter formatter;

    protected AdoptionService(AdoptionRepository<S, T> adoptionRepository, PersonService<T> personService,
                              PetService petService) {
        this.adoptionRepository = adoptionRepository;
        this.personService = personService;
        this.petService = petService;
        formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    }

    public Collection<S> getAllAdoptions(){
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
    public List<S> getAllActiveProbations(){
        return getAllAdoptionsByStatus(AdoptionStatus.ON_PROBATION, AdoptionStatus.PROBATION_EXTENDED);
    }

    /**
     * Finds all {@link Adoption} object records having adoption_status values withing the values in parameters
     * @param adoptionStatuses - array of {@link AdoptionStatus} to be included in selection
     * @return {@link List} of {@link Adoption} objects
     */
    public List<S> getAllAdoptionsByStatus(AdoptionStatus ... adoptionStatuses){
        return adoptionRepository.findAllByAdoptionStatusIn(adoptionStatuses);
    }

    public S findById(int id){
        return adoptionRepository.findById(id).orElse(null);
    }

    public S findByChatId(long chatId){
        T person = personService.findPersonByChatId(chatId)
                .orElseThrow(PersonNotFoundException::new);
        return adoptionRepository.findByPerson(person);
    }

    public S findByPetId(int petId) {
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
    public S findByPersonId(int personId) {
        T person = personService.findById(personId);
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
    public S save(S adoption){
        if (findByPersonId(adoption.getPerson().getId()) != null || findByPetId(adoption.getPet().getId()) != null) {
            return null;
        }
        return adoptionRepository.save(adoption);
    }

    public S edit(S adoption) {
        S adoptionFound = findById(adoption.getId());
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

    public S setNewStatus(int adoptionId, AdoptionStatus adoptionStatus){
        S adoption = findById(adoptionId);
        return setNewStatus(adoption, adoptionStatus);
    }

    public S setNewStatus(S adoption, AdoptionStatus adoptionStatus){
        if (adoption != null){
            adoption.setAdoptionStatus(adoptionStatus);
            adoptionRepository.save(adoption);
        }
        return adoption;
    }

    public S setNewProbationEndDate(int adoptionId, String newDate) {
        S adoption = findById(adoptionId);
        LocalDate reportDate = LocalDate.parse(newDate, formatter);
        return setNewProbationEndDate(adoption, reportDate);
    }
    public S setNewProbationEndDate(S adoption, LocalDate newDate) {
        if (adoption != null) {
            adoption.setProbationEndDate(newDate);
            adoptionRepository.save(adoption);
        }
        return adoption;
    }
}
