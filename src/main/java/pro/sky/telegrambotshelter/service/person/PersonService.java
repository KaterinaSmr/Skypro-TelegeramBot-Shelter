package pro.sky.telegrambotshelter.service.person;

import pro.sky.telegrambotshelter.model.person.Person;
import pro.sky.telegrambotshelter.repository.PersonDogRepository;
import pro.sky.telegrambotshelter.repository.PersonRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * A Service class to perform CRUD operations with the "person" table in database.
 * @author Ekaterina Gorbacheva
 * @see PersonCatService
 * @see PersonDogService
 */
public abstract class PersonService <T extends Person> {

    private final PersonRepository<T> personRepository;

    protected PersonService(PersonRepository<T> personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Finds a {@link Person} object record in the "person_cat" / "person_dog" with the chatId specified.
     * Uses {@link PersonDogRepository}.
     * @param chatId telegram chat identification for a {@link Person} object
     * @return {@link Optional<Person>} of the search result
     */
    public Optional<T> findPersonByChatId(long chatId){
        return personRepository.findByChatId(chatId);
    }

    /**
     * Saves a new {@link Person} object to a "person_cat" or "person_dog" table. Uses {@link PersonDogRepository}.
     * The  field in the new {@link Person} object should be unique. If the table already contains a record
     * with the same chat_id value, then the new record is not saved, and this method returns {@code null}.
     * @param person new {@link Person} object to be saved
     * @return {@link Person} object just saved, or {@code null}. in case of duplicated chat_id
     */
    public T save(T person) {
        if (findPersonByChatId(person.getChatId()).isPresent()){
            return null;
        }
        return personRepository.save(person);
    }

    /**
     * Returns {@link Person} object from db table "person_cat" or "person_dog" by person ID. Uses {@link PersonDogRepository}.
     * @param personId identification of a person to be found
     * @return Person object with the id specified, or {@code null}. if not found
     */
    public T findById(int personId) {
        return personRepository.findById(personId).orElse(null);
    }

    /**
     * A method to get all records from the db table "person_cat" or "person_dog". Uses {@link PersonDogRepository}
     * @return {@link Collection} of all {@link Person} objects saved in db table "person"
     */
    public Collection<T> findAll() {
        return personRepository.findAll();
    }

    /**
     * A method for saving updates of a {@link Person} object to the db table "person_cat" or "person_dog".
     * Uses {@link PersonDogRepository}
     * @param person {@link Person} object with updates to be saved
     * @return {@link Person} object with updated fields, or {@code null} if a person with the id specified is not found.
     * @throws org.hibernate.exception.ConstraintViolationException in case of duplicated chatId
     */
    public T edit(T person) {
        T personFound = findById(person.getId());
        if (personFound == null){
            return null;
        }
        return personRepository.save(person);
    }

    /**
     * Removes a record from the "person" table with this id. Uses {@link PersonDogRepository}
     * @param id identification of a {@link Person} to be removed from db table "person_cat" or "person_dog"
     */
    public void delete(Integer id) {
        personRepository.deleteById(id);
    }
}
